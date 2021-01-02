package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.rift.Rift;
import com.ascendancyproject.ascendnations.rift.RiftConfig;

public class NationPower {
    private int maxMemberPower;
    private int maxRiftPower;

    private int memberPower;
    private int riftPower;

    private int claimThreshold;
    private int existenceThreshold;

    private int chunksClaimable;
    private int outpostsClaimable;

    private long lockoutExpiry;
    private long chunkLockoutExpiry;

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
        for (Long chunk : nation.getChunks()) {
            Rift rift = RiftConfig.getRift(chunk);

            if (rift != null && rift.isCheckChunk(chunk) && rift.isOwned(nation))
                riftPower += rift.getPower();
        }

        riftPower = Math.min(riftPower, maxRiftPower);

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

        boolean shouldLockout = claimThreshold > getTotal();
        boolean shouldChunkLockout = nation.getChunks().size() > chunksClaimable || nation.getOutposts().size() > outpostsClaimable;

        if (lockoutExpiry == 0L) {
            if (shouldLockout) {
                nation.broadcast(Language.format("nationLockout", new String[]{"nationName", nation.getName()}));
                lockoutExpiry = System.currentTimeMillis() + nv.getLockoutDuration();
            }
        } else if (!shouldLockout) {
            nation.broadcast(Language.format("nationLockoutExit", new String[]{"nationName", nation.getName()}));
            lockoutExpiry = 0L;
        }

        if (chunkLockoutExpiry == 0L) {
            if (shouldChunkLockout) {
                nation.broadcast(Language.format("nationLockoutChunk", new String[]{"nationName", nation.getName()}));
                chunkLockoutExpiry = System.currentTimeMillis() + nv.getLockoutChunkDuration();
            }
        } else if (!shouldChunkLockout) {
            nation.broadcast(Language.format("nationLockoutChunkExit", new String[]{"nationName", nation.getName()}));
            chunkLockoutExpiry = 0L;
        }
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

    public long getLockoutExpiry() {
        return lockoutExpiry;
    }

    public long getChunkLockoutExpiry() {
        return chunkLockoutExpiry;
    }
}
