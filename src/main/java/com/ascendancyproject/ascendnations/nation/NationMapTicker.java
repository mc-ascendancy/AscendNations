package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NationMapTicker {
    private static final int tickFrequency = 20;

    public NationMapTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, tickFrequency, tickFrequency);
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasMetadata(NationMapMetadata.key))
                continue;

            NationMapMetadata metadata = (NationMapMetadata) player.getMetadata(NationMapMetadata.key).get(0);
            if (!metadata.isExpired())
                continue;

            metadata.resetBlocks(player);
            Language.sendMessage(player, "nationMapExpired");

            player.removeMetadata(NationMapMetadata.key, AscendNations.getInstance());
        }
    }
}
