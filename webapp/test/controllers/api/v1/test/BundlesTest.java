package controllers.api.v1.test;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import models.Project;

import org.junit.Test;

import play.libs.F.Callback;
import play.mvc.Result;
import play.test.TestBrowser;

public class BundlesTest {

    @Test
    public void index() {
	running(testServer(7331, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	    public void invoke(TestBrowser browser) {
		Project.Key key = new Project.Key("test.project", "one");
		Project.create(key);
		Result index = callAction(new controllers.api.v1.ref.ReverseBundles().index(key));
		
		System.out.println(contentAsString(index));
		assertThat(status(index)).isEqualTo(200);
		assertThat(contentType(index)).isEqualTo("application/json");
		assertThat(contentAsString(index)).contains("\"bundles\":[]");
	    }
	});
    }

    @Test
    public void create() {
	running(testServer(7331, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	    public void invoke(TestBrowser browser) {
		Project.Key key = new Project.Key("test.project", "one");
		Project.create(key);
		
		Result createMessages = callAction(new controllers.api.v1.ref.ReverseBundles().create(key, "messages"));
		Result createFormats = callAction(new controllers.api.v1.ref.ReverseBundles().create(key, "formats"));
		assertThat(status(createMessages)).isEqualTo(CREATED);
		assertThat(status(createFormats)).isEqualTo(CREATED);
		
		Result index = callAction(new controllers.api.v1.ref.ReverseBundles().index(key));

		browser.goTo("http://localhost:7331/api/v1/test.project:one/bundles/");
		assertThat(status(index)).isEqualTo(OK);
		assertThat(contentAsString(index)).contains("\"name\":\"messages\"");
		assertThat(contentAsString(index)).contains("\"name\":\"formats\"");
	    }
	});
    }

}
