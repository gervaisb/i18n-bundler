package models;

import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.Json;

@Entity
public class Translation extends Model {
    private static final long serialVersionUID = 6001348935358707161L;

    public static final Locale FRENCH = new Locale("fr", "", "");
    public static final Locale DUTCH  = new Locale("nl", "", "");
    
    @Id
    private Long id;
    
    @Required @Pattern("[\\.A-Za-z0-9_-]*")
    public String key;

    private String dutch;
    private String french;
    
    @ManyToOne(cascade = CascadeType.REFRESH)
    private Bundle bundle;
    
        
    public Translation(final String key) {
	this.key = key;
    }
    
    public Translation in(final Locale locale, final String value) {
	if ( DUTCH.equals(locale) ) {
	    dutch = value;
	} else if ( FRENCH.equals(locale) ) {
	    french = value;
	} else {
	    throw new UnsupportedOperationException("Translations in "+locale);
	}
	return this;
    }
    
    public Translation inFrench(final String value) {
	return in(FRENCH, value);
    }

    public Translation inDutch(final String value) {
	return in(DUTCH, value);
    }

    public String toFrench() {
	return to(FRENCH);
    }
    
    public String toDutch() {
	return to(DUTCH);
    }
    
    public String to(final Locale language) {
	if ( DUTCH.equals(language) ) {
	    return dutch;
	} else if ( FRENCH.equals(language) ) {
	    return french;
	} else {
	    return new StringBuilder(key.length()+10).append("?? ")
		    .append(language.getLanguage()).append("_").append(key).append(" ??")
		    .toString();
	}
    }
    
    public boolean hasFrench() {
	return french!=null;
    }
    
    public boolean hasDutch() {
	return dutch!=null;
    }
    
    public boolean has(final Locale locale) {
	return ( locale.getLanguage().equals(FRENCH.getLanguage()) && hasFrench() ) ||
	       ( locale.getLanguage().equals(DUTCH.getLanguage()) && hasDutch() );
	       
    }
    
    public ObjectNode toJSon() {
	final ObjectNode json = Json.newObject();
	json.put("key", key);

	final ArrayNode values = json.arrayNode();
	json.put("values", values);
	
	final ObjectNode fr = values.objectNode();
	fr.put("lang", FRENCH.getLanguage());
	fr.put("text", toFrench());
	values.add(fr);

	final ObjectNode nl = values.objectNode();
	nl.put("lang", DUTCH.getLanguage());
	nl.put("text", toDutch());
	values.add(nl);
	
	return json;
    }
    
    @Override
    public String toString() {
	return key;
    }
    
    @Override
    public boolean equals(Object other) {
	if ( this==other )
	    return true;
	if ( !(other instanceof Translation) )
	    return false;
	final Translation that = (Translation) other;
        return 	key!=null?key.equals(that.key):that.key==null;
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
}
