package controllers.ui;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import models.Bundle;
import models.Project;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.Helper;
import controllers.Mimes;

public class Application extends Controller {
    
    public static Result home() {
	final List<Project> projects = Project.all();
	return ok(views.html.home.render(projects));
    }
    
    public static Result showNewProjectDialog() {
	return ok(views.html.projects._dialog.render(Helper.form(Project.class)))
			.as(Mimes.Application.JAVASCRIPT);
    }
    
    public static Result editProject(final Project.Key key) {
	try {
	    final Project project = Project.get(key);
	    return ok(views.html.projects.edit.render(project));
	} catch (final EntityNotFoundException notFound) {
	    return notFound(views.html.projects.notfound.render(key));
	}
    }
    
    public static Result showNewBundleDialog(final Project.Key key) {
	return ok(views.html.bundles._dialog.render(Helper.form(Bundle.class), key))
		.as(Mimes.Application.JAVASCRIPT);
    }
    
    public static Result showBundlePartial(final Project.Key project, final String name) {
	try {
	    final Bundle bundle = Bundle.get(project, name);
	    return ok(views.html.bundles._content.render(bundle));
	} catch (EntityNotFoundException notFound) {
	    return notFound(views.html.bundles._notfound.render(project, name));
	}
    }
    
    public static Result showUpdateTranslationDialog(final Project.Key project, final String bundleName, final String key) {
	try {
	    final Bundle bundle = Bundle.get(project, bundleName);
	    if ( !bundle.contains(key) ) {
		throw new EntityNotFoundException("No translation found inside \""+bundle+"\" for key \""+key+"\".");
	    }
	    
	    return ok(views.html.translations._dialog.render(bundle.get(key).get(), bundle))
		    .as(Mimes.Application.JAVASCRIPT);
	} catch (EntityNotFoundException notFound) {
	    return notFound(notFound.getMessage());
	}
    }
    
}
