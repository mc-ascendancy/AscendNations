package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.UUID;

public class ClaimChunks {
    public static HashMap<Long, UUID> chunks;

    public static void init() {
        chunks = new HashMap<>();

        World world = Bukkit.getWorld("world");

        for (Nation nation : PersistentData.instance.getNations().values()) {
            for (Long chunk : nation.getChunks())
                chunks.put(chunk, nation.getUUID());

            if (nation.getHome() != null)
                world.getBlockAtKey(nation.getHome()).setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Home));

            for (Long outpost : nation.getOutposts().keySet())
                world.getBlockAtKey(outpost).setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Outpost));
        }
    }

    public static boolean hasNeighbour(UUID nationUUID, Location location) {
        return checkChunk(nationUUID, location.getChunk().getX() - 1, location.getChunk().getZ())
                || checkChunk(nationUUID, location.getChunk().getX() + 1, location.getChunk().getZ())
                || checkChunk(nationUUID, location.getChunk().getX(), location.getChunk().getZ() - 1)
                || checkChunk(nationUUID, location.getChunk().getX(), location.getChunk().getZ() + 1);
    }

    public static boolean checkChunk(UUID nationUUID, int x, int z) {
        return checkChunk(nationUUID, Chunk.getChunkKey(x, z));
    }

    public static boolean checkChunk(UUID nationUUID, Long key) {
        UUID chunkUUID = ClaimChunks.chunks.get(key);
        return chunkUUID != null && chunkUUID.equals(nationUUID);
    }

    public static void claim(Nation nation, Long key) {
        chunks.put(key, nation.getUUID());
        nation.getChunks().add(key);
    }

    public static void claim(Nation attackingNation, Nation defendingNation, Long key) {
        claim(attackingNation, key);
        defendingNation.getChunks().remove(key);
    }

    public static void unclaim(Nation nation, Long key) {
        chunks.remove(key);
        nation.getChunks().remove(key);
    }
}
