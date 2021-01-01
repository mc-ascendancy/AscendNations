package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OverclaimEvents implements Listener {
    public OverclaimEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getFrom().getChunk().equals(event.getTo().getChunk()) && Overclaim.failOverclaim(event.getPlayer()))
            event.getPlayer().sendMessage(Language.getLine("overclaimFailMoved"));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getFrom().getChunk().equals(event.getTo().getChunk()) && Overclaim.failOverclaim(event.getPlayer()))
            event.getPlayer().sendMessage(Language.getLine("overclaimFailMoved"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Overclaim.failOverclaim(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && Overclaim.failOverclaim((Player) event.getEntity()))
            event.getEntity().sendMessage(Language.getLine("overclaimFailDamaged"));
    }
}
