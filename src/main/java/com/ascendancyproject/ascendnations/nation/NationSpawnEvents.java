package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.concurrent.ThreadLocalRandom;

public class NationSpawnEvents implements Listener {
    public NationSpawnEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn())
            return;

        event.setRespawnLocation(randomSpawn(event.getRespawnLocation().getWorld()));
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        if (event.getPlayer().hasPlayedBefore())
            return;

        event.setSpawnLocation(randomSpawn(event.getSpawnLocation().getWorld()));
    }

    private Location randomSpawn(World world) {
        WorldBorder border = world.getWorldBorder();

        int size = (int)(border.getSize() / 2);

        int xMin = border.getCenter().getBlockX() - size;
        int xMax = border.getCenter().getBlockX() + size;
        int zMin = border.getCenter().getBlockZ() - size;
        int zMax = border.getCenter().getBlockZ() + size;

        Block spawn = null;
        while (!isGoodSpawn(spawn)) {
            int x = ThreadLocalRandom.current().nextInt(xMin, xMax + 1);
            int z = ThreadLocalRandom.current().nextInt(zMin, zMax + 1);

            spawn = world.getHighestBlockAt(x, z);
        }

        return spawn.getLocation().add(0.5, 1, 0.5);
    }

    private boolean isGoodSpawn(@Nullable Block block) {
        if (block == null)
            return false;

        if (!NationVariables.instance.getGoodSpawnBlocks().contains(block.getType().name()))
            return false;

        return !ClaimChunks.chunks.containsKey(block.getChunk().getChunkKey());
    }
}
