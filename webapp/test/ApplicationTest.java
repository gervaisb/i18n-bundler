
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;





/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void open_new_project_dialog() {
	running(testServer(4567), HTMLUNIT, new Callback<TestBrowser>() {
	    @Override
	    public void invoke(TestBrowser browser) throws Throwable {
	        browser.goTo("http://localhost:4567");
	        FluentWebElement link = browser.$(".navbar .nav a").get(0); 
	        assertThat(link.getText()).isEqualTo("New");
	        
	        link.click();
	        assertThat(browser.$("#project-create-dialog")).isNotNull();
	    }
	});
    }
   
}
