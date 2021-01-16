package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationPermission;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

public class ClaimProtectionEvents implements Listener {
    public ClaimProtectionEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        new ClaimProtectionHopperEvents(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if (event.hasItem() && event.getItem().getType().equals(Material.BONE_MEAL))
            return;

        if (!NationVariables.instance.getProtectedBlocks().contains(event.getClickedBlock().getType().name()) &&
                !AscendNationsHelper.isRedstone(event.getClickedBlock().getType()))
            return;

        if (blockProtectedPlayer(event.getClickedBlock(), event.getPlayer(), true, true, false))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (int i = event.blockList().size() - 1; i >= 0; i--)
            if (blockProtected(event.blockList().get(i)))
                event.blockList().remove(i);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (blockProtectedPlayer(event.getBlock(), event.getPlayer(), true, false, true))
                event.setCancelled(true);
        } else {
            if (blockProtected(event.getBlock()))
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (blockProtectedPlayer(event.getBlock(), event.getPlayer(), false, false, true))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (blockProtectedPlayer(event.getBlock(), event.getPlayer(), true, false, true))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (blockProtectedPlayer(event.getBlock(), (Player) event.getEntity(), true, false, true))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player &&
                NationVariables.instance.getProtectedMobs().contains(event.getEntity().getType().name()) &&
                entityProtected(event.getEntity(), (Player) event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                NationVariables.instance.getProtectedMobs().contains(event.getEntity().getType().name()) &&
                entityProtected(event.getEntity(), null))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType().equals(EntityType.SILVERFISH))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (entityProtected(event.getRightClicked(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (blockPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection(), true))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (blockPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection(), false))
            event.setCancelled(true);
    }

    private boolean blockProtected(Block block) {
        return ClaimBlock.isClaimBlock(block)
                || ClaimChunks.chunks.containsKey(block.getLocation().getChunk().getChunkKey())
                || RiftConfig.getRift(block.getChunk().getChunkKey()) != null;
    }

    private boolean blockProtectedPlayer(Block block, Player player, boolean protectClaim, boolean redstone, boolean rift) {
        if (rift && RiftConfig.getRift(block.getChunk().getChunkKey()) != null) {
            Language.sendMessage(player, "blockProtectedRift");
            return true;
        }

        if (protectClaim && ClaimBlock.isClaimBlock(block)) {
            Language.sendMessage(player, "blockProtectedClaim");
            return true;
        }

        UUID nationUUID = ClaimChunks.chunks.get(block.getChunk().getChunkKey());

        if (nationUUID == null)
            return false;

        Nation nation = PersistentData.instance.getNations().get(nationUUID);

        if (nation.getMembers().containsKey(player.getUniqueId()))
            return false;

        if (redstone && AscendNationsHelper.isRedstone(block.getType())) {
            if (nation.getPermissions().contains(NationPermission.Redstone))
                return false;

            Language.sendMessage(player, "blockProtectedRedstone", new String[]{"nationName", nation.getName()});
            return true;
        }

        Language.sendMessage(player, "blockProtected", new String[]{"nationName", nation.getName()});

        return true;
    }

    private boolean entityProtected(Entity entity, Player player) {
        UUID nationUUID = ClaimChunks.chunks.get(entity.getChunk().getChunkKey());

        if (nationUUID == null)
            return false;

        if (player == null)
            return true;

        Nation nation = PersistentData.instance.getNations().get(nationUUID);

        if (nation.getMembers().containsKey(player.getUniqueId()))
            return false;

        Language.sendMessage(player, "entityProtected", new String[]{"nationName", nation.getName()});

        return true;
    }

    private boolean blockPistonEvent(Block origin, List<Block> affected, BlockFace direction, boolean push) {
        for (Block block : affected)
            if (ClaimBlock.isClaimBlock(block) || RiftConfig.getRift(block.getChunk().getChunkKey()) != null)
                return true;

        UUID fromUUID = ClaimChunks.chunks.get(origin.getChunk().getChunkKey());

        for (Block block : affected) {
            if (push)
                block = block.getRelative(direction);

            UUID toUUID = ClaimChunks.chunks.get(block.getChunk().getChunkKey());
            if (toUUID != null && !toUUID.equals(fromUUID))
                return true;
        }

        return false;
    }
}
