package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import org.bukkit.event.Listener;

public class NationOutpostEvents implements Listener {
    public NationOutpostEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
