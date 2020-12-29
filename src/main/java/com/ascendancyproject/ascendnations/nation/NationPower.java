package com.ascendancyproject.ascendnations.nation;

public class NationPower {
    private int maxMemberPower;
    private int maxRiftPower;

    private int memberPower;
    private int riftPower;

    public NationPower(Nation nation) {
        recalculate(nation);
    }

    public void recalculate(Nation nation) {
        int pop = nation.getMembers().size();

        maxMemberPower = pop * NationVariables.instance.getMaxMemberPower();
        maxRiftPower = Math.max(pop * NationVariables.instance.getRiftModifier() - NationVariables.instance.getMinNationPopClaimRift() * NationVariables.instance.getRiftModifier(), 0);

        memberPower = 0;
        for (NationMember member : nation.getMembers().values())
            memberPower += member.getPower().getTotal();

        riftPower = 0;
        // TODO: add rift power calculation once claiming is implemented.
    }

    public int getMaxPower() {
        return maxMemberPower + maxRiftPower;
    }

    public int getMaxMemberPower() {
        return maxMemberPower;
    }

    public int getMaxRiftPower() {
        return maxRiftPower;
    }

    public int getTotal() {
        return memberPower + riftPower;
    }

    public int getMemberPower() {
        return memberPower;
    }

    public int getRiftPower() {
        return riftPower;
    }
}
