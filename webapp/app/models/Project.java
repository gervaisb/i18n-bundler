package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.PathBindable;

@Entity
public class Project extends Model implements Iterable<Bundle> {
    private static final long serialVersionUID = -3420193371864749138L;

    private static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static List<Project> all() {
	return find.select("id, groupId, artifactId").findList();
    }

    public static Project create(final Key key) {
	final Project created = new Project(key.groupId, key.artifactId);
	created.save();
	return created;
    }

    public static Project get(final Key key) {
	final Project project = find
		.fetch("bundles")
		.where().idEq(key)
		.findUnique();
	if (project == null) {
	    throw new EntityNotFoundException("No project found for \""+key+"\".");
	}
	return project;
    }

    // ~ ------------------------------------------------------------------ ~ //

    @Embeddable
    public static class Key implements PathBindable<Key> {

	@Required
	@Pattern("([a-z0-9-_]\\.?)+")
	public String groupId;
	@Required
	@Pattern("([a-z0-9-_]\\.?)+")
	public String artifactId;

	public Key() {}
	
	public Key(final Project project) {
	    this.artifactId = project.key.artifactId;
	    this.groupId = project.key.groupId;
	}

	public Key(final String groupId, final String artifactId) {
	    this.artifactId = artifactId;
	    this.groupId = groupId;
	}

	@Override
	public String toString() {
	    return new StringBuilder(groupId.length() + artifactId.length() + 1)
	    	.append(groupId).append(':').append(artifactId).toString();
	}

	@Override
	public Key bind(final String key, final String value) {
	    final String[] parts = value.split(":");
	    this.artifactId = parts[1];
	    this.groupId = parts[0];
	    return this;
	}

	@Override
	public String unbind(final String key) {
	    return toString();
	}

	@Override
	public String javascriptUnbind() {
	    return toString();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if ( obj==this )
		return true;
	    if ( !(obj instanceof Project.Key) )
		return false;
	    final Project.Key that = (Project.Key) obj;
	    return (groupId!=null
		    	?groupId.equals(that.groupId)
		    	:that.groupId==null) &&
     		   (artifactId!=null
		    	?artifactId.equals(that.artifactId)
		    	:that.artifactId==null);   	
	}

	@Override
	public int hashCode() {
	    return (groupId==null?1:groupId.hashCode())+
		    (artifactId==null?1:artifactId.hashCode())
		    *2;
	}
    }
    
    // ~ ------------------------------------------------------------------ ~ //

    @EmbeddedId
    private Key key;

    @OneToMany(cascade = CascadeType.PERSIST)
    public List<Bundle> bundles = new ArrayList<Bundle>();

    public Project() {
	
    }
    
    private Project(String groupId, String artifactId) {
	this.key = new Key(groupId, artifactId);
    }
    
    @Override
    public Iterator<Bundle> iterator() {
        return bundles!=null
        	?bundles.iterator()
        	:Collections.<Bundle>emptyList().iterator();
    }
    
    public Key getKey() {
	return key;
    }
    
    public String getGroupId() {
	return key.groupId;
    }
    
    public String getArtifactId() {
	return key.artifactId;
    }

    @Override
    public String toString() {
        return key.toString();
    }
    
    public ObjectNode toJSon() {
	final ObjectNode json = Json.newObject();
	final ArrayNode bundles = json.arrayNode();
	json.put("groupId", key.groupId);
	json.put("artifactId", key.artifactId);
	json.put("bundles", bundles);
	for (final Bundle bundle : this) {
	    ObjectNode bundleJs = bundles.objectNode();
	    bundleJs.put("name", bundle.name);
	    bundleJs.put("size", bundle.size());
	}
	return json;
    }
    
    @Override
    public int hashCode() {
        return key!=null?key.hashCode():0;
    }
    
    @Override
    public boolean equals(Object other) {
        if ( this==other )
            return true;
        if ( !(other instanceof Project) )
            return false;
        final Project that = (Project) other;
        return	key!=null?key.equals(that.key):that.key==null;
    }
    
}
