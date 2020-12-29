package com.ascendancyproject.ascendnations.nation;

public class NationMember {
    private NationRole role;
    private final NationMemberPower power;
    
    public NationMember(NationRole role) {
        this.role = role;
        power = new NationMemberPower();
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
