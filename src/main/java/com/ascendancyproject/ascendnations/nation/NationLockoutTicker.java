package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.rift.RiftConfig;

import java.util.HashSet;
import java.util.Iterator;

public class NationLockoutTicker {
    public NationLockoutTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, NationVariables.instance.getLockoutReminderFrequency(), NationVariables.instance.getLockoutReminderFrequency());
    }

    private void tick() {
        for (Nation nation : PersistentData.instance.getNations().values()) {
            if (nation.getPower().getLockoutExpiry() != 0)
                nation.broadcast("nationLockoutReminder", new String[]{"nationName", nation.getName()});

            if (nation.getPower().getChunkLockoutExpiry() != 0) {
                if (nation.getPower().getChunkLockoutExpiry() < System.currentTimeMillis())
                    reclaimChunks(nation);
                else
                    nation.broadcast("nationLockoutChunkReminder", new String[]{"nationName", nation.getName()});
            }
        }
    }

    private void reclaimChunks(Nation nation) {
        int startingOutposts = nation.getOutposts().size();
        int startingChunks = nation.getChunks().size();

        while (nation.getOutposts().size() > nation.getPower().getOutpostsClaimable()) {
            Long outpost = nation.getOutposts().keySet().iterator().next();
            nation.getOutposts().get(outpost).destroy(nation, outpost);
            nation.getOutposts().remove(outpost);
        }

        while (nation.getChunks().size() > nation.getPower().getChunksClaimable()) {
            Iterator<Long> it = nation.getChunks().iterator();
            Long remove = it.next();
            if (nation.isClaimChunk(remove) || RiftConfig.getRift(remove) != null)
                remove = it.next();

            HashSet<Long> touched = nation.getTouched(remove);
            nation.recalculateChunks(touched);
        }

        nation.getPower().recalculate(nation);
        nation.setClaimPunishmentExpiry(System.currentTimeMillis() + NationVariables.instance.getClaimPunishmentDuration());

        nation.broadcast("nationReclaimedChunks",
                new String[]{"chunkCount", Integer.toString(startingChunks - nation.getChunks().size())},
                new String[]{"outpostCount", Integer.toString(startingOutposts - nation.getOutposts().size())},
                new String[]{"claimPunishmentDuration", AscendNationsHelper.durationToString(NationVariables.instance.getClaimPunishmentDuration())}
        );
    }
}
