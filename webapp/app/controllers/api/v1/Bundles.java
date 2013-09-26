package controllers.api.v1;

import static controllers.Helper.locate;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import models.Bundle;
import models.Project;
import models.Translation;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.Mimes;
import export.Export;

public class Bundles extends Controller {
    
    private static final Logger LOG = LoggerFactory.getLogger(Bundles.class);
    
    public static Result index(final Project.Key projectKey) {	
	final List<Bundle> bundles = Bundle.findByProject(projectKey);
	final ObjectNode responseJs = Json.newObject();
	final ArrayNode  bundlesJs  = responseJs.arrayNode();
	for (final Bundle bundle : bundles) {
	    final ObjectNode bundleJs = Json.newObject();
	    bundleJs.put("self", locate(bundle));
	    bundleJs.put("name", bundle.name);
	    bundleJs.put("project", locate(bundle.project));
	    bundleJs.put("size", bundle.size());
	}
	responseJs.put("project", locate(Project.class, projectKey.groupId, projectKey.artifactId));
	responseJs.put("bundles", bundlesJs);
	return ok(responseJs);
    }
        
    public static Result create(final Project.Key projectKey, final String name) {
	LOG.info("Creating bundle {} in {}.", name, projectKey);
	try {
	    final Project project = Project.get(projectKey);
	    final Bundle bundle = Bundle.create(name, project);
	    
	    final ObjectNode json = bundle.toJSon();
	    json.put("self", locate(bundle));
	    json.put("project", locate(bundle.project));
	    
	    response().setHeader("Location", locate(bundle));
	    return created(json);
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }

    public static Result show(final Project.Key project, final String name) throws IOException {
	final Pattern patternForFormat = Pattern.compile("((_[a-z]{2})?\\.properties)|(\\.xls)");
	final Matcher matcherForFormat = patternForFormat.matcher(name);	
	try {
	    if (matcherForFormat.find()) {
		final Bundle bundle = Bundle.get(project, name.substring(0, matcherForFormat.start()));
		return export(bundle, matcherForFormat.group());
	    } else {
		final Bundle bundle = Bundle.get(project, name);
		return show(bundle);
	    }
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }
    
    private static Result show(final Bundle bundle) {
	try {
	    final ObjectNode bundleJs = Json.newObject();
	    final ArrayNode  linesJs = bundleJs.arrayNode(); 
	    bundleJs.put("self", locate(bundle));
	    bundleJs.put("name", bundle.name);
	    bundleJs.put("size", bundle.size());
	    bundleJs.put("project", locate(bundle.project));
	    bundleJs.put("lines", linesJs);
	    for (final Translation line : bundle) {
		linesJs.add(line.toJSon());
	    }
	    return ok(bundleJs);
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }
    
    public static Result export(final Bundle bundle, final String format) throws IOException {
	try {
	    return ok(new Export(bundle).as(format))
		    .as(Mimes.MICROSOFT_EXCEL);
	    
	} catch (UnsupportedOperationException unsupported) {
	    return status(NOT_ACCEPTABLE, unsupported.getMessage());
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }

}
