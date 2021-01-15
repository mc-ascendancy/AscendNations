package com.ascendancyproject.ascendnations.nation;

public class NationMember {
    private NationRole role;
    
    public NationMember(NationRole role) {
        this.role = role;
    }

    public NationRole getRole() {
        return role;
    }

    public void setRole(NationRole role) {
        this.role = role;
    }
}
