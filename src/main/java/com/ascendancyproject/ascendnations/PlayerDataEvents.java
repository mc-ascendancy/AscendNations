package com.ascendancyproject.ascendnations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerDataEvents implements Listener {
    public PlayerDataEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!PersistentData.instance.getPlayers().containsKey(event.getPlayer().getUniqueId()))
            PersistentData.instance.getPlayers().put(event.getPlayer().getUniqueId(), new PlayerData());
    }
}
