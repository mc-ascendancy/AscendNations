package com.ascendancyproject.ascendnations.nation;

public class NationPower {
    private int maxMemberPower;
    private int maxRiftPower;

    private int memberPower;
    private int riftPower;

    private int claimThreshold;
    private int existenceThreshold;

    private int chunksClaimable;
    private int outpostsClaimable;

    public NationPower(Nation nation) {
        recalculate(nation);
    }

    public void recalculate(Nation nation) {
        int pop = nation.getMembers().size();
        NationVariables nv = NationVariables.instance;

        maxMemberPower = pop * nv.getMaxMemberPower();
        maxRiftPower = Math.max(pop * nv.getRiftModifier() - nv.getMinNationPopClaimRift() * nv.getRiftModifier(), 0);

        memberPower = 0;
        for (NationMember member : nation.getMembers().values())
            memberPower += member.getPower().getTotal();

        riftPower = 0;
        // TODO: add rift power calculation once claiming is implemented.

        if (pop == 1) {
            claimThreshold = nv.getClaimThresholdOnePlayer();
            existenceThreshold = nv.getExistenceThresholdOnePlayer();
        } else if (pop <= nv.getMinNationPopClaimRift()) {
            claimThreshold = nv.getClaimThresholdSmallModifier() * pop - nv.getClaimThresholdSmallOffset();
            existenceThreshold = nv.getExistenceThresholdSmallModifier() * pop - nv.getExistenceThresholdSmallOffset();
        } else {
            claimThreshold = (nv.getClaimThresholdLargeModifier() * pop)
                    + (nv.getClaimThresholdSmallModifier() * nv.getMinNationPopClaimRift() - nv.getClaimThresholdSmallOffset())
                    - (nv.getClaimThresholdLargeModifier() * nv.getMinNationPopClaimRift());

            existenceThreshold = claimThreshold - (nv.getExistenceThresholdLargeModifierDynamic() * pop) + nv.getExistenceThresholdLargeModifierFlat();
        }

        chunksClaimable = (int)(pop * nv.getChunksPerMember());
        outpostsClaimable = (int)(pop * nv.getOutpostsPerMember());
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

    public int getClaimThreshold() {
        return claimThreshold;
    }

    public int getExistenceThreshold() {
        return existenceThreshold;
    }

    public int getChunksClaimable() {
        return chunksClaimable;
    }

    public int getOutpostsClaimable() {
        return outpostsClaimable;
    }
}
