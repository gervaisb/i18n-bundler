package services.impl.mymemory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Akka;
import play.libs.F.Promise;
import services.Translator;

/**
 * {@link Translator} implementation who use the free MyMemory service.
 * 
 * @see http://mymemory.translated.net/doc/spec.php
 *
 */
public class MymemoryTranslator implements Translator {

    private static final Logger LOG = LoggerFactory.getLogger(MymemoryTranslator.class);
    
    private static final String URL_FORMAT = "http://api.mymemory.translated.net/get?q=%1$s&langpair=en|%2$s";
    private static final String KEYWORD = "translatedText";
    private static final String CHARSET = "UTF-8";
    private static final int SUPPOSED_LENGTH_UNTIL_KEYWORD = 35;
	    
    @Override
    public Promise<String> translate(final String sentence, final Locale to) {
	return Akka.future(new Callable<String>() {
	    @Override
	    public String call() throws Exception {
		InputStreamReader response = null;
		String translation = null;
		try {
		    URL url = new URL(String.format(URL_FORMAT, URLEncoder.encode(sentence, CHARSET), to.getLanguage()));

		    response = new InputStreamReader(url.openStream(), CHARSET);
		    translation = parse(response);
		} catch (UnknownHostException offline) {
		    LOG.info("Failed to transate [{}]. The application seems to be offline : {}.", sentence, offline.getMessage());
		} catch (SocketException offline) {
		    LOG.info("Failed to transate [{}]. The application seems to be offline : {}.", sentence, offline.getMessage());
		} catch (IOException e) {
		   e.printStackTrace();
		   throw new Error(e);
		} finally {
		    if ( response!=null ) { try { response.close(); } catch (IOException ioe) { /*ignore*/ } }
		}

	        return translation;
	    }
	});
    }
    
    private String parse(InputStreamReader reader) throws IOException {
	StringBuilder json = new StringBuilder(SUPPOSED_LENGTH_UNTIL_KEYWORD*2);
	char[] chars = new char[SUPPOSED_LENGTH_UNTIL_KEYWORD];
	int index = -1;
	int reads = -1;
	while ( (index = json.indexOf(KEYWORD))<0 && (reads = reader.read(chars))>-1 ) {
	    json.append(chars, 0, reads);
	}
	json.delete(0, json.length());
	while ( (index = json.indexOf("\"", index))<0 && (reads = reader.read(chars))>-1 ) {
	    json.append(chars, 0, reads);
	}
	
	String value = json.substring(0, index);
	value = StringEscapeUtils.unescapeHtml4(value);
	value = StringEscapeUtils.unescapeJava(value);
	return json.length()==0?null:value;
    }
    
    
}
