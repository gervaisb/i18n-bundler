package export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import models.Bundle;
import models.Translation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Export {
    private static final Logger LOG = LoggerFactory.getLogger(Export.class);
    private static final int AVERAGE_LINE_LENGTH = 40;
    
    private final Bundle bundle;
    
    public Export(final Bundle bundle) {
	this.bundle = bundle;
    }
    
    public InputStream as(final String format) throws IOException {
	return as(format, Charset.forName("UTF-8"));
    }
    
    public InputStream as(final String extension, final Charset charset) throws IOException {
	final ByteArrayOutputStream memory = new ByteArrayOutputStream(bundle.size()*AVERAGE_LINE_LENGTH);
	final Format format = FormatFactory.createFormat(extension, memory);
	try {
	    format.open(bundle);
	    for (final Translation translation : bundle) {
		format.append(translation);
	    }
	    format.close();
	    return new ByteArrayInputStream(memory.toByteArray());
	} catch (IOException ioe) {
	    LOG.warn("Failed to export \"{}\" to \"{}\" : {}.", new Object[]{
		    bundle, format, ioe.getMessage()});
	    throw ioe;
	}
    }

}
