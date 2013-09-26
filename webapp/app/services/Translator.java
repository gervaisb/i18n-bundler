package services;

import java.util.Locale;

import play.libs.F.Promise;

public interface Translator {

    Promise<String> translate(final String sentence, final Locale to);
    
}
