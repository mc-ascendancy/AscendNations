package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class ClaimProtectionEvents implements Listener {
    public ClaimProtectionEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;

        assert event.getClickedBlock() != null;

        if (blockProtectedPlayer(event.getClickedBlock(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (int i = event.blockList().size() - 1; i >= 0; i--)
            if (blockProtected(event.blockList().get(i)))
                event.blockList().remove(i);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (blockProtectedPlayer(event.getBlock(), event.getPlayer()))
                event.setCancelled(true);
        } else {
            if (blockProtected(event.getBlock()))
                event.setCancelled(true);
        }
    }

    private boolean blockProtected(Block block) {
        return ClaimBlock.isClaimBlock(block) || ClaimChunks.chunks.containsKey(block.getLocation().getChunk().getChunkKey());
    }

    private boolean blockProtectedPlayer(Block block, Player player) {
        if (ClaimBlock.isClaimBlock(block)) {
            player.sendMessage(Language.getLine("blockProtectedClaim"));
            return true;
        }

        UUID nationUUID = ClaimChunks.chunks.get(block.getChunk().getChunkKey());

        if (nationUUID == null)
            return false;

        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (nationUUID.equals(playerData.getNationUUID()))
            return false;

        Nation nation = PersistentData.instance.getNations().get(nationUUID);
        player.sendMessage(Language.format("blockProtected", new String[]{"nationName", nation.getName()}));

        return true;
    }
}
