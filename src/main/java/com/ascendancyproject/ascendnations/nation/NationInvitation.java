package com.ascendancyproject.ascendnations.nation;

import java.util.UUID;

public class NationInvitation {
    private static final int invitationDuration = 60 * 1000;

    private final UUID playerUUID;
    private final UUID nationUUID;
    private final long expiry;

    public NationInvitation(UUID playerUUID, UUID nationUUID) {
        this.playerUUID = playerUUID;
        this.nationUUID = nationUUID;
        expiry = System.currentTimeMillis() + invitationDuration;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getNationUUID() {
        return nationUUID;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() >= expiry;
    }

    public long getExpiry() {
        return expiry;
    }
}
