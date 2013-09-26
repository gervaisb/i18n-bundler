package export;

import java.io.IOException;

import models.Bundle;
import models.Translation;

public interface Format {
    
    void open(final Bundle bundle) throws IOException;
    
    void append(final Translation translation) throws IOException;
    
    void close() throws IOException;
}