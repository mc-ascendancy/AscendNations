package com.ascendancyproject.ascendnations.nation;

import java.util.UUID;

public class NationMember {
    private NationRole role;
    private final NationMemberPower power;
    
    public NationMember(NationRole role, UUID playerUUID, Nation nation) {
        this.role = role;
        power = new NationMemberPower(nation.getMembersJoined().add(playerUUID));
    }

    public NationRole getRole() {
        return role;
    }

    public void setRole(NationRole role) {
        this.role = role;
    }

    public NationMemberPower getPower() {
        return power;
    }
}
