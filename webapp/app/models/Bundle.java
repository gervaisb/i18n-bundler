package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.Json;

import com.avaje.ebean.FetchConfig;
import com.google.common.base.Optional;

@Entity
public class Bundle extends Model implements Iterable<Translation> {
    private static final long serialVersionUID = 8225799815994001763L;
    
    private static Finder<Long, Bundle> find = new Finder<Long, Bundle>(Long.class, Bundle.class);

    public static Bundle get(final Project.Key project, String name) {
	final Bundle bundle = find
		.fetch("project", new FetchConfig().query())
		.where()
			.eq("group_id", project.groupId)
			.eq("artifact_id", project.artifactId)
			.eq("name", name)
		.findUnique();
	if ( bundle==null ) {
	    throw new EntityNotFoundException("No bundle named \""+name+"\" found into project \""+project+"\".");
	}
	return bundle;
    }
    
    public static List<Bundle> findByProject(final Project.Key groupAndArtifact) {
	return find
		.fetch("project")
		.where()
			.eq("project.groupId", groupAndArtifact.groupId)
			.eq("project.artifactId", groupAndArtifact.artifactId)
		.orderBy("name")
		.findList();
    }

    public static Bundle create(final String name, final Project owner) {
	final Bundle bundle = new Bundle(name, owner);
	bundle.save();
	return bundle;    
    }
    
    // ~ ------------------------------------------------------------------ ~ //

    @Id
    protected Long id;

    @Required @Pattern("\\w") 
    public String name;    
    
    @OneToMany(cascade=CascadeType.ALL)
    private List<Translation> translations = new ArrayList<Translation>();
    
    @ManyToOne(cascade=CascadeType.ALL)
    public Project project;

    private Bundle(final String name, final Project project) {
	this.project = project;
	this.name = name;
	
	this.project.bundles.add(this);
	this.translations = new ArrayList<Translation>();
    }
    
    public int size() {
	return translations!=null?translations.size():0;
    }

    public Translation put(final String key) {
	return put(new Translation(key));
    }
    
    public Translation put(Translation translation) {
	if ( contains(translation.key) ) {
	    Translation existing = get(translation.key).get();
	    existing.inFrench(translation.toFrench())
		    .inDutch(translation.toDutch())
		    .update();
	} else {
	    translations.add(translation);
	    save();
	}	
	return translation;
    }    
    
    public Optional<Translation> get(final String key) {
	for (final Translation translation : translations) {
	    if ( translation.key.equals(key) ) {
		return Optional.of(translation);
	    }
	}
	return Optional.absent();
    }

    public boolean contains(final String key) {
	return get(key).isPresent();
    }

    public boolean isEmpty() {
	return translations==null||translations.isEmpty();
    }
    
    public List<Translation> getTranslations() {
	return translations!=null
        	?translations
        	:Collections.<Translation>emptyList();
    }
    
    @Override
    public Iterator<Translation> iterator() {
        return getTranslations().iterator();
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
        	.append(project).append('\\').append(name).toString();
    }
    
    public ObjectNode toJSon() {
	final ObjectNode json = Json.newObject();
	final ArrayNode lines = json.arrayNode();
	json.put("name", name);
	json.put("size", size());
	json.put("lines", lines);
	for (final Translation line : this) {
	    lines.add(line.toJSon());
	}
	return json;
    }

    public void remove(String key) {
	final Optional<Translation> found = get(key);
	if ( found.isPresent() ) {
	    translations.remove(found.get());
	    found.get().delete();
	}
    }

}
