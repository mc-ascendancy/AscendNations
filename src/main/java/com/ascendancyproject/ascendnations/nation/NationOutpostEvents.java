package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

public class NationOutpostEvents implements Listener {
    public NationOutpostEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        PersistentDataContainer data = event.getVehicle().getPersistentDataContainer();
        Double health = data.get(NationOutpost.nbtKeyHealth, PersistentDataType.DOUBLE);

        if (health == null)
            return;

        health -= event.getDamage();

        if (health > 0) {
            event.setDamage(0);
            data.set(NationOutpost.nbtKeyHealth, PersistentDataType.DOUBLE, health);
            return;
        }

        Long outpost = data.get(NationOutpost.nbtKeyOutpost, PersistentDataType.LONG);
        if (outpost == null) {
            // This should never happen, it would mean only half of the NBT data is present; just reset.
            event.getVehicle().remove();
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(ClaimChunks.chunks.get(Nation.getOutpostChunk(outpost)));
        nation.getOutposts().get(outpost).spawnMinecart(nation, outpost, true, false);

        event.getVehicle().remove();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        spawnUnspawnedMinecarts(event);

        // Despawn expired minecarts.
        for (Entity entity : event.getChunk().getEntities()) {
            PersistentDataContainer data = entity.getPersistentDataContainer();
            Long key = data.get(NationOutpost.nbtKeyOutpost, PersistentDataType.LONG);

            if (key == null)
                continue;

            UUID nationUUID = ClaimChunks.chunks.get(Nation.getOutpostChunk(key));
            if (nationUUID == null) {
                entity.remove();
                continue;
            }

            Nation nation = PersistentData.instance.getNations().get(nationUUID);
            NationOutpost outpost = nation.getOutposts().get(key);

            if (outpost == null || outpost.getMinecartUUID() != entity.getUniqueId())
                entity.remove();
        }
    }

    private void spawnUnspawnedMinecarts(ChunkLoadEvent event) {
        Long key = event.getChunk().getChunkKey();

        UUID nationUUID = ClaimChunks.chunks.get(key);
        if (nationUUID == null)
            return;

        Nation nation = PersistentData.instance.getNations().get(nationUUID);

        // Spawn in Minecarts that weren't spawned in due to the home chunk being unloaded.
        for (Map.Entry<Long, NationOutpost> entry : nation.getOutposts().entrySet())
            if (entry.getValue().getResupplyState().equals(NationOutpostResupply.InProgress) && entry.getValue().getMinecartUUID() == null)
                entry.getValue().spawnMinecart(nation, entry.getKey(), false, true);
    }
}
