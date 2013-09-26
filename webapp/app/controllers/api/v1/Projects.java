package controllers.api.v1;

import static controllers.Helper.locate;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import models.Project;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Projects extends Controller {
    
    public static Result index() {
	final List<Project> projects = Project.all();

	final ObjectNode responseJs = Json.newObject();
	final ArrayNode projectsJs = responseJs.arrayNode();
	for (final Project project : projects) {
	    final ObjectNode projectJs = Json.newObject();
	    projectJs.put("href", locate(project));
	    projectJs.put("groupId", project.getGroupId());
	    projectJs.put("artifactId", project.getArtifactId());
	    projectsJs.add(projectJs);
	}
	responseJs.put("projects", projectsJs);
	return ok(responseJs);
    }

    public static Result create(final Project.Key key) {
    	final Project project = Project.create(key);
    	final ObjectNode projectJS = Json.newObject();    	
    	projectJS.put("self", locate(project));
    	projectJS.put("groupId", project.getGroupId());
    	projectJS.put("artifactId", project.getArtifactId());
    	projectJS.put("bundles", projectJS.arrayNode());

    	response().setHeader("Location", locate(project));
    	return created(projectJS);
    }    
    
    public static Result show(final Project.Key key) {
	try {
	    final Project project = Project.get(key);
	    final ObjectNode json = project.toJSon();
	    json.put("self", locate(project));	    
	    return ok(json);
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }
  
}
