package stories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.InvalidStoryResource;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;

abstract class Story extends JUnitStory {

    @Override
    public Configuration configuration() {
	Keywords keywords = new LocalizedKeywords(Locale.ENGLISH);
	return new MostUsefulConfiguration()
		.useKeywords(keywords)
		.useStoryParser(new RegexStoryParser(keywords))
		.useStoryLoader(new CustomRelativeStoryLoader())
		.useStoryReporterBuilder(new StoryReporterBuilder()
			.withDefaultFormats()
			.withFormats(Format.CONSOLE, Format.HTML)
			.withFailureTrace(true)
			.withKeywords(keywords)
		);
    }

    public List<Object> generateStepsInstances() {
	final List<Object> steps = new ArrayList<Object>(1);
	addDefaultSteps(steps);	
	return steps;
    }

    protected void addDefaultSteps(List<Object> steps) {
	final String defaultStepsName = new StringBuilder(getClass().getName().length()+5)
        	.append(getClass().getName()).append("Steps").toString();
        try {
            Class<?> defaultStepsClass = Class.forName(defaultStepsName);
            steps.add(defaultStepsClass.newInstance());      
        } catch (InstantiationException e) {
            System.err.println("[error] Cannot instantiate default steps "+defaultStepsName+" : "+e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("[error] Cannot instantiate default steps "+defaultStepsName+" : "+e.getMessage());
        } catch (ClassNotFoundException ignore) {
            System.out.println("Not default setps founds. ["+ignore.getMessage()+"]");
            /* Ignore silently when no default steps are found */
        }	
    }

    @Override
    public List<CandidateSteps> candidateSteps() {
	return new InstanceStepsFactory(configuration(), generateStepsInstances()).createCandidateSteps();
    }
  
    private class CustomRelativeStoryLoader implements StoryLoader {

	private File testDirectory = new File(new File(System.getProperty("user.dir")).getAbsoluteFile(), "test");

	@Override
	public String loadStoryAsText(final String storyName) {
	    final File location = locate(storyName);
	    return load(location);
	}

	private File locate(final String story) {
	    // story = "stories\<lower_snake_cased_name_of_the_story>.story"
	    final StringBuilder filename = new StringBuilder(new File(story).getName());
	    for (int i = 0; i < filename.length(); i++) {
		char chr = filename.charAt(i);
		if (i == 0 || filename.charAt(i - 1) == '_') {
		    filename.setCharAt(i, Character.toUpperCase(chr));
		    if (i > 0 && filename.charAt(i - 1) == '_') {
			filename.deleteCharAt(i - 1);
		    }
		}
	    }
	    return new File(testDirectory, filename.insert(0, "stories/").toString());
	}

	private String load(final File location) {
	    final StringBuilder buffer = new StringBuilder(1024);
	    InputStreamReader stream = null;
	    try {
		stream = new InputStreamReader(new FileInputStream(location), Charset.defaultCharset());
		char[] chars = new char[512];
		int reads = -1;
		while ((reads = stream.read(chars)) != -1) {
		    buffer.append(chars, 0, reads);
		}
	    } catch (final IOException ioe) {
		throw new InvalidStoryResource(location.getAbsolutePath(), ioe);
	    } finally {
		if (stream != null) { try { stream.close(); } catch (IOException ignore) { /**/ } }
	    }
	    return buffer.toString();
	}
    }
  
} 
