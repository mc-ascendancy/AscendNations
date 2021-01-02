package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.language.Language;
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
                nation.broadcast(Language.format("nationLockoutReminder", new String[]{"nationName", nation.getName()}));

            if (nation.getPower().getChunkLockoutExpiry() != 0) {
                if (nation.getPower().getChunkLockoutExpiry() < System.currentTimeMillis())
                    reclaimChunks(nation);
                else
                    nation.broadcast(Language.format("nationLockoutChunkReminder", new String[]{"nationName", nation.getName()}));
            }
        }
    }

    private void reclaimChunks(Nation nation) {
        int startingChunks = nation.getChunks().size();

        while (nation.getPower().getChunkLockoutExpiry() != 0) {
            Iterator<Long> it = nation.getChunks().iterator();
            Long remove = it.next();
            if (nation.isClaimChunk(remove) || RiftConfig.getRift(remove) != null)
                remove = it.next();

            HashSet<Long> touched = nation.getTouched(remove);
            nation.recalculateChunks(touched);

            nation.getPower().recalculate(nation);
        }

        nation.broadcast(Language.format("nationReclaimedChunks", new String[]{"chunkCount", Integer.toString(startingChunks - nation.getChunks().size())}));
    }
}
