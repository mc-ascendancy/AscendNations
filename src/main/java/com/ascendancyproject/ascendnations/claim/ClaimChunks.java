package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class ClaimChunks {
    public static HashMap<Long, UUID> chunks;

    public static void init() {
        chunks = new HashMap<>();

        for (Nation nation : PersistentData.instance.getNations().values())
            for (Long chunk : nation.getChunks())
                chunks.put(chunk, nation.getUUID());
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
