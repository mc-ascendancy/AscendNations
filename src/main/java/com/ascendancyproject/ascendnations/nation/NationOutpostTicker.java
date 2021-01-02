package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;

import java.util.Map;

public class NationOutpostTicker {
    private static final int tickFrequency = 20;

    public NationOutpostTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, tickFrequency, tickFrequency);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::reminderTick, NationVariables.instance.getResupplyReminderFrequency(), NationVariables.instance.getResupplyReminderFrequency());
    }

    private void tick() {
        for (Nation nation : PersistentData.instance.getNations().values())
            for (Map.Entry<Long, NationOutpost> entry : nation.getOutposts().entrySet())
                entry.getValue().tick(nation, entry.getKey());
    }

    private void reminderTick() {
        for (Nation nation : PersistentData.instance.getNations().values())
            for (Map.Entry<Long, NationOutpost> entry : nation.getOutposts().entrySet())
                entry.getValue().reminderTick(nation);
    }
}
