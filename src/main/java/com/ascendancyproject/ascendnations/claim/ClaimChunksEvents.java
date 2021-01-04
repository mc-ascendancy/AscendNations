package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class ClaimChunksEvents implements Listener {
    public ClaimChunksEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().equals(event.getTo().getChunk()))
            return;

        UUID fromUUID = ClaimChunks.chunks.get(event.getFrom().getChunk().getChunkKey());
        UUID toUUID = ClaimChunks.chunks.get(event.getTo().getChunk().getChunkKey());

        if (fromUUID != null && !fromUUID.equals(toUUID)) {
            Nation nation = PersistentData.instance.getNations().get(fromUUID);
            event.getPlayer().sendMessage(Language.getLine("prefix") + " " + nation.getMessages().get("exit"));
        }

        if (toUUID != null && !toUUID.equals(fromUUID)) {
            Nation nation = PersistentData.instance.getNations().get(toUUID);
            event.getPlayer().sendMessage(Language.getLine("prefix") + " " + nation.getMessages().get("entry"));
        }
    }
}
