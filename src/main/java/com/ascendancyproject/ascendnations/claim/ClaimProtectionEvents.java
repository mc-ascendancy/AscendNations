package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

        if (blockProtectedPlayer(event.getClickedBlock(), event.getPlayer(), true))
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
            if (blockProtectedPlayer(event.getBlock(), event.getPlayer(), true))
                event.setCancelled(true);
        } else {
            if (blockProtected(event.getBlock()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (blockProtectedPlayer(event.getBlock(), event.getPlayer(), false))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player &&
                NationVariables.instance.getProtectedMobs().contains(event.getEntity().getType().name()) &&
                entityProtected(event.getEntity(), (Player) event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType().equals(EntityType.SILVERFISH))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (entityProtected(event.getRightClicked(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getChunk().equals(event.getToBlock().getChunk()))
            return;

        UUID toUUID = ClaimChunks.chunks.get(event.getToBlock().getChunk().getChunkKey());
        if (toUUID == null)
            return;

        UUID fromUUID = ClaimChunks.chunks.get(event.getBlock().getChunk().getChunkKey());
        if (toUUID.equals(fromUUID))
            return;

        event.setCancelled(true);
    }

    private boolean blockProtected(Block block) {
        return ClaimBlock.isClaimBlock(block) || ClaimChunks.chunks.containsKey(block.getLocation().getChunk().getChunkKey());
    }

    private boolean blockProtectedPlayer(Block block, Player player, boolean protectClaimBlock) {
        if (protectClaimBlock && ClaimBlock.isClaimBlock(block)) {
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

    private boolean entityProtected(Entity entity, Player player) {
        UUID nationUUID = ClaimChunks.chunks.get(entity.getChunk().getChunkKey());

        if (nationUUID == null)
            return false;

        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (nationUUID.equals(playerData.getNationUUID()))
            return false;

        Nation nation = PersistentData.instance.getNations().get(nationUUID);
        player.sendMessage(Language.format("entityProtected", new String[]{"nationName", nation.getName()}));

        return true;
    }
}
