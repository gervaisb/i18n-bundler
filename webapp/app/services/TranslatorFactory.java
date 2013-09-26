package services;

import services.impl.mymemory.MymemoryTranslator;

public class TranslatorFactory {

    public Translator create() {
	return new MymemoryTranslator();
    }
    
}
