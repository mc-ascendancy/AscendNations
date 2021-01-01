package com.ascendancyproject.ascendnations.rift;

import com.ascendancyproject.ascendnations.AscendNations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class RiftConfig {
    public static final String location = "rifts.json";

    private static ArrayList<Rift> rifts;
    private static HashMap<Long, Integer> riftMap;

    public static void init(File file, AscendNations plugin) {
        if (!file.exists())
            plugin.saveResource(location, false);

        Gson gson = new Gson();

        try {
            rifts = gson.fromJson(new FileReader(file), new TypeToken<ArrayList<Rift>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        riftMap = new HashMap<>();

        for (int i = 0; i < rifts.size(); i++) {
            for (RiftChunk chunk : rifts.get(i).getChunks()) {
                Long key = Chunk.getChunkKey(chunk.getX(), chunk.getZ());

                rifts.get(i).setCheckChunkIfEmpty(key);
                riftMap.put(key, i);
            }
        }
    }

    public static @Nullable Rift getRift(Long key) {
        Integer pos = riftMap.get(key);
        return pos == null ? null : rifts.get(pos);
    }
}
