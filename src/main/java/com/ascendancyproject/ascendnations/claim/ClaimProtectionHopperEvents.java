package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ClaimProtectionHopperEvents implements Listener {
    private static final NamespacedKey ownerKey = new NamespacedKey(AscendNations.getInstance(), "an-owner");

    public ClaimProtectionHopperEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void addHopperMinecartMetadata(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getItem().getType().equals(Material.HOPPER_MINECART))
            return;

        if (!event.hasBlock() || !AscendNationsHelper.isRail(event.getClickedBlock().getType()))
            return;

        event.setCancelled(true);
        event.getItem().setAmount(event.getItem().getAmount() - 1);

        HopperMinecart minecart = event.getClickedBlock().getWorld().spawn(event.getClickedBlock().getLocation().add(0.5, 0, 0.5), HopperMinecart.class);

        PersistentDataContainer data = minecart.getPersistentDataContainer();
        data.set(ownerKey, PersistentDataType.LONG_ARRAY, new long[]{
                event.getPlayer().getUniqueId().getMostSignificantBits(),
                event.getPlayer().getUniqueId().getLeastSignificantBits()
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!(event.getDestination().getHolder() instanceof HopperMinecart))
            return;

        if (!(event.getSource().getHolder() instanceof Container))
            return;

        Container container = (Container) event.getSource().getHolder();
        UUID nationUUID = ClaimChunks.chunks.get(container.getBlock().getChunk().getChunkKey());

        if (nationUUID == null)
            return;

        HopperMinecart minecart = (HopperMinecart) event.getDestination().getHolder();
        PersistentDataContainer data = minecart.getPersistentDataContainer();

        long[] ownerLongArray = data.get(ownerKey, PersistentDataType.LONG_ARRAY);

        if (ownerLongArray == null) {
            event.setCancelled(true);
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(nationUUID);
        UUID owner = new UUID(ownerLongArray[0], ownerLongArray[1]);

        if (!nation.getMembers().containsKey(owner))
            event.setCancelled(true);
    }
}
