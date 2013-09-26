package export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import models.Bundle;
import models.Translation;

public class Properties implements Format {

    private final Locale locale;
    private final OutputStream out;
    
    private java.util.Properties properties = null;
    private String comments = null;
    
    Properties(final String extension, final OutputStream out) {
	this(
		new Locale(extension.substring(1, extension.indexOf('.'))),
		out);
    }
    
    public Properties(final Locale locale, final OutputStream out) {
	this.locale = new Locale(locale.getLanguage(), "", "");
	this.out = out;
    }
    
    @Override
    public void open(Bundle bundle) throws IOException {
	properties = new java.util.Properties();   
	comments = new StringBuilder("Export of ")
		.append(bundle.toString()).append(" created by i18n bundler.")
		.toString();
    }
    
    @Override
    public void append(Translation translation) throws IOException {
	if ( translation.has(locale) ) {
	    properties.put(translation.key, translation.to(locale));
	}
    }
    
    @Override
    public void close() throws IOException {
        properties.store(out, comments);
        out.flush();
    }
    
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
        	.append('_').append(locale).append(".properties")
        	.toString();
    }

}
