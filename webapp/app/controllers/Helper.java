package controllers;

import models.Bundle;
import models.Project;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http.Context;

public abstract class Helper {

    public static <M> Form<M> form(final Class<M> model) {
   	return new Form<M>(model);
    }
    
    public static String locate(final Project project) {
	return locate(Project.class, project.getGroupId(), project.getArtifactId());
    }
    public static String locate(Class<Project> class1, String groupId, String artifactId) {
	final Project.Key project = new Project.Key(groupId, artifactId);
	return absolute(controllers.api.v1.routes.Projects.show(project));
    }

    public static String locate(final Bundle bundle) {
	final Project.Key project = bundle.project.getKey();
	return absolute(controllers.api.v1.routes.Bundles.show(project, bundle.name));
    }

    private static String absolute(final Call call) {
	return call.absoluteURL(Context.current().request()).intern();
    }

}
