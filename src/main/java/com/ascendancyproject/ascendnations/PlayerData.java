package com.ascendancyproject.ascendnations;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerData {
    private UUID nationUUID;

    public @Nullable UUID getNationUUID() {
        return nationUUID;
    }

    public void setNationUUID(@Nullable UUID nationUUID) {
        this.nationUUID = nationUUID;
    }
}
