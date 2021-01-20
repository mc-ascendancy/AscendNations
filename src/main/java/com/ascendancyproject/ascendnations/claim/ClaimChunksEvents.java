package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class ClaimChunksEvents implements Listener {
    public ClaimChunksEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onPlayerMove(PlayerMoveEvent event) {
        move(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        move(event.getPlayer(), event.getFrom(), event.getTo());
    }

    private void move(Player player, Location from, Location to) {
        if (from.getChunk().equals(to.getChunk()))
            return;

        UUID fromUUID = ClaimChunks.chunks.get(from.getChunk().getChunkKey());
        UUID toUUID = ClaimChunks.chunks.get(to.getChunk().getChunkKey());

        if (fromUUID != null && !fromUUID.equals(toUUID)) {
            Nation nation = PersistentData.instance.getNations().get(fromUUID);
            player.sendMessage(Language.format(player, "prefix") + " " + nation.getMessages().get("exit"));
        }

        if (toUUID != null && !toUUID.equals(fromUUID)) {
            Nation nation = PersistentData.instance.getNations().get(toUUID);
            player.sendMessage(Language.format(player, "prefix") + " " + nation.getMessages().get("entry"));
        }
    }
}
