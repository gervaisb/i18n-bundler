package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

public class BundleTest {

    @Test
    public void findByProject() {
      running(fakeApplication(inMemoryDatabase()), new Runnable() {
        public void run() {
            Project projectOne = Project.create(new Project.Key("tst.project", "one"));
            Project projectTwo = Project.create(new Project.Key("tst.project", "two"));
            
            Bundle.create("messages_1a", projectOne);

            Bundle.create("messages_2a", projectTwo);
            Bundle.create("messages_2b", projectTwo);
            
            List<Bundle> bundlesForOne = Bundle
        	    .findByProject(new Project.Key(projectOne));
            assertThat(bundlesForOne.size()).isSameAs(1);
            assertThat(bundlesForOne.get(0).name).isEqualTo("messages_1a");

            List<Bundle> bundlesForTwo = Bundle
        	    .findByProject(new Project.Key(projectTwo));
            assertThat(bundlesForTwo.size()).isSameAs(2);
            assertThat(bundlesForTwo.get(0).name).isEqualTo("messages_2a");
            assertThat(bundlesForTwo.get(1).name).isEqualTo("messages_2b");
        }
      });
    }
    
}
