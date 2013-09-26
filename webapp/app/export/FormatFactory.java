package export;

import java.io.OutputStream;

public abstract class FormatFactory {

    public static Format createFormat(final String extension, final OutputStream out) {
	if (".xls".equals(extension)) {
	    return new Excel(out);
	} else if (extension.matches("_[a-z]{2}\\.properties")) {
	    return new Properties(extension, out);
	}
	throw new UnsupportedOperationException("Export to \"" + extension + "\" is not suppported.");
    }

}