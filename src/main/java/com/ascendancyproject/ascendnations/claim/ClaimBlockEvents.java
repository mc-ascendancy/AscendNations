package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationRole;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ClaimBlockEvents implements Listener {
    public ClaimBlockEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ClaimBlock.isClaimBlock(event.getItemInHand()))
            return;

        PlayerData playerData = PersistentData.instance.getPlayers().get(event.getPlayer().getUniqueId());

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        if (nation == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        if (nation.lacksPermissions(event.getPlayer().getUniqueId(), NationRole.Commander)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Commander.name()}));
            return;
        }

        Long key = event.getBlock().getChunk().getChunkKey();

        if (ClaimChunks.chunks.containsKey(key)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.getLine("errorChunkClaimAlreadyOwned"));
            return;
        }

        if (RiftConfig.getRift(key) != null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.getLine("errorClaimSpecialOnRift"));
            return;
        }

        boolean success;

        if (event.getBlock().getType() == NationVariables.instance.getClaimBlockHome())
            success = ClaimBlock.placeHome(event.getBlock(), event.getPlayer(), nation);
        else
            success = ClaimBlock.placeOutpost(event.getBlock(), event.getPlayer(), nation);

        if (!success)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!ClaimBlock.isClaimBlock(event.getItemDrop().getItemStack()))
            return;

        ClaimBlock.removedBlock(event.getPlayer(), event.getItemDrop().getItemStack());
        event.getItemDrop().remove();
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!ClaimBlock.isClaimBlock(event.getEntity().getItemStack()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !ClaimBlock.isClaimBlock(event.getCurrentItem()))
            return;

        ClaimBlock.removedBlock((Player) event.getWhoClicked(), event.getCurrentItem());
        event.setCurrentItem(null);
    }
}
