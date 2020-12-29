package com.ascendancyproject.ascendnations.nation;

public class NationMember {
    private NationRole role;
    private NationMemberPower power;
    
    public NationMember(NationRole role) {
        this.role = role;
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

    public void setPower(NationMemberPower power) {
        this.power = power;
    }
}
