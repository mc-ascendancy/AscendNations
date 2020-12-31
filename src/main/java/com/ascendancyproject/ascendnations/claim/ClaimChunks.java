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

            world.getBlockAtKey(nation.getHome()).setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Home));
        }
    }

    public static boolean hasNeighbour(UUID nationUUID, Location location) {
        return checkChunk(nationUUID, location.getChunk().getX() - 1, location.getChunk().getZ())
                || checkChunk(nationUUID, location.getChunk().getX() + 1, location.getChunk().getZ())
                || checkChunk(nationUUID, location.getChunk().getX(), location.getChunk().getZ() - 1)
                || checkChunk(nationUUID, location.getChunk().getX(), location.getChunk().getZ() + 1);
    }

    private static boolean checkChunk(UUID nationUUID, int x, int y) {
        UUID chunkUUID = ClaimChunks.chunks.get(Chunk.getChunkKey(x, y));
        return chunkUUID != null && chunkUUID.equals(nationUUID);
    }

    public static boolean claim(Nation nation, Location location) {
        Long key = location.getChunk().getChunkKey();

        if (chunks.containsKey(key))
            return false;

        chunks.put(key, nation.getUUID());
        nation.getChunks().add(key);
        return true;
    }
}
