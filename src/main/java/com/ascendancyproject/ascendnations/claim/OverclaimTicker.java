package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;

import java.util.Map;
import java.util.UUID;

public class OverclaimTicker {
    public OverclaimTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, Overclaim.tickFrequency, Overclaim.tickFrequency);
    }

    private void tick() {
        for (Map.Entry<UUID, Overclaim> entry : Overclaim.overclaims.entrySet())
            entry.getValue().tick(entry.getKey());
    }
}
