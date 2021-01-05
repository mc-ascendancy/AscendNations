package com.ascendancyproject.ascendnations.language;

import java.util.HashMap;

public class LanguageConfig {
    private String defaultLanguage;
    private HashMap<String, LanguageLang> languages;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public HashMap<String, LanguageLang> getLanguages() {
        return languages;
    }
}
