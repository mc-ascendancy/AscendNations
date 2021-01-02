package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.language.Language;

public class NationLockoutTicker {
    public NationLockoutTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, NationVariables.instance.getLockoutReminderFrequency(), NationVariables.instance.getLockoutReminderFrequency());
    }

    private void tick() {
        for (Nation nation : PersistentData.instance.getNations().values()) {
            if (nation.getPower().getLockoutExpiry() != 0)
                nation.broadcast(Language.format("nationLockoutReminder", new String[]{"nationName", nation.getName()}));

            if (nation.getPower().getChunkLockoutExpiry() != 0)
                nation.broadcast(Language.format("nationLockoutChunkReminder", new String[]{"nationName", nation.getName()}));
        }
    }
}
