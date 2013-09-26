package stories;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import models.Bundle;
import models.Project;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import play.libs.F.Callback;
import play.mvc.Result;
import play.test.TestBrowser;
import play.test.TestServer;

public class CanViewBundlesInProjectSteps {

    private TestServer server;

    private Project project;
    private Bundle bundle;
    private Result index;

    @Given("the application contains the '$projectKey' project with a '$bundleName' bundle")
    public void createProjectAndBundle(final String projectKey, final String bundleName) {
	final String[] groupAndArtifact = projectKey.split(":");
	server = testServer(7331, fakeApplication(inMemoryDatabase("test")));
	running(server, HTMLUNIT, new Callback<TestBrowser>() {
	    @Override
	    public void invoke(TestBrowser browser) throws Throwable {
		project = Project.create(new Project.Key(groupAndArtifact[0], groupAndArtifact[1]));
		bundle = Bundle.create(bundleName, project);
	    }
	});
    }

    @When("I open the project resource")
    public void openTheProjectIndex() {
	running(server, HTMLUNIT, new Callback<TestBrowser>() {
	    @Override
	    public void invoke(TestBrowser browser) throws Throwable {
		index = callAction(new controllers.api.v1.ref.ReverseProjects().show(project.getKey()));
	    }
	});
    }

    @Then("the response should contains a list of a single bundle named '$bundleName'")
    public void resultShouldContainsANamedBundle(final String bundleName) {
	running(server, HTMLUNIT, new Callback<TestBrowser>() {
	    @Override
	    public void invoke(TestBrowser browser) throws Throwable {
		assertThat(status(index)).isEqualTo(OK);
		assertThat(contentAsString(index)).contains(bundleName);
	    }
	});
    }

}
