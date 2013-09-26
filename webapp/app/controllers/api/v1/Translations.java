package controllers.api.v1;

import static controllers.Helper.locate;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.persistence.EntityNotFoundException;

import models.Bundle;
import models.Project;
import models.Translation;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.Translator;
import services.TranslatorFactory;

public class Translations extends Controller {
    
    private static final Translator TRANSLATOR = new TranslatorFactory().create();
    
    public static Result put(final Project.Key project, final String bundelName, final String key, final Boolean translate) {
	try {
	    final Bundle bundle = Bundle.get(project, bundelName);
	    
	    final ObjectNode json = Json.newObject();
	    json.put("self", locate(bundle)+"/"+key);
	    json.put("key", key);
	    
	    final JsonNode body = request().body().asJson();

	    Promise<Result> promise = Akka.future(new Callable<Result>() {
		@Override
    	    	public Result call() throws Exception {
		    final Translation translation = new Translation(key);

		    JsonNode values = body.findPath("values");
		    String french = getTextValue(values, "fr");
		    String dutch = getTextValue(values, "nl");

		    if (french == null && translate) {
			translation.inFrench(translate(key, Translation.FRENCH));
		    } else {
			translation.inFrench(french);
		    }

		    if (dutch == null && translate) {
			translation.inDutch(translate(key, Translation.DUTCH));
		    } else {
			translation.inDutch(dutch);
		    }

		    final ObjectNode valuesJs = json.putObject("values");
		    valuesJs.put("fr", translation.toFrench());
		    valuesJs.put("nl", translation.toDutch());

		    bundle.put(translation);

		    return ok(json);	    
    	    	}
	    });
	    return async(promise);
	} catch (final EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }

    private static String getTextValue(final JsonNode values, final String language) {
	String text = null;
	if ( values.findPath(language)==null || (text = values.findPath(language).getTextValue())==null ) {
	    return null;
	} 
	return text.trim().isEmpty()?null:text.trim();
    }
    
    private static String translate(final String key, final Locale locale) {
	final String sentence = key.replace('.', ' ');
	return TRANSLATOR.translate(sentence, locale).get();
    }
            
    public static Result delete(final Project.Key project, final String bundelName, final String key) {
	try {
	    final Bundle bundle = Bundle.get(project, bundelName);
	    if ( bundle.contains(key) ) {
		bundle.remove(key);
		return ok();
	    } else {
		return notFound("No translation for \""+key+"\" was found into \""+bundle+"\".");
	    }
	} catch (final EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }
    

}
