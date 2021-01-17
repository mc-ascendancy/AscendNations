package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.NationMemberPower;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerData {
    private UUID nationUUID;

    private String language;
    private boolean languageDefined;
    private boolean nationChat;

    private final NationMemberPower power;

    public PlayerData() {
        language = Language.config.getDefaultLanguage();
        power = new NationMemberPower();
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

    public NationMemberPower getPower() {
        return power;
    }

    public boolean isNationChat() {
        return nationChat;
    }

    public void setNationChat(boolean nationChat) {
        this.nationChat = nationChat;
    }
}
