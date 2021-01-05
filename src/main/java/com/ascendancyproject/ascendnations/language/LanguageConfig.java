package com.ascendancyproject.ascendnations.language;

import java.util.HashMap;

public class LanguageConfig {
    private String defaultLanguage;
    private HashMap<String, String> languageNames;
    private HashMap<String, HashMap<String, String>> lines;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public HashMap<String, String> getLanguageNames() {
        return languageNames;
    }

    public HashMap<String, HashMap<String, String>> getLines() {
        return lines;
    }
}
