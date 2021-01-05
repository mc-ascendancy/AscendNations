package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.language.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerData {
    private UUID nationUUID;

    private String language;
    private boolean languageDefined;

    public PlayerData() {
        language = Language.config.getDefaultLanguage();
    }

    public @Nullable UUID getNationUUID() {
        return nationUUID;
    }

    public void setNationUUID(@Nullable UUID nationUUID) {
        this.nationUUID = nationUUID;
    }

    public @NotNull String getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull String language) {
        this.language = language;
    }

    public boolean isLanguageDefined() {
        return languageDefined;
    }

    public void setLanguageDefined(boolean languageDefined) {
        this.languageDefined = languageDefined;
    }
}
