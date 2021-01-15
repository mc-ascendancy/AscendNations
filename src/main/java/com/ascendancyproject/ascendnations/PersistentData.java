package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class PersistentData {
    public static final String location = "nations.json";

    private final HashMap<UUID, Nation> nations;
    private final HashMap<UUID, PlayerData> players;

    public static PersistentData instance;

    public PersistentData() {
        nations = new HashMap<>();
        players = new HashMap<>();
    }

    public static void init(File file, AscendNations plugin) {
        Gson gson = new Gson();

        if (file.exists()) {
            try {
                instance = gson.fromJson(new FileReader(file), new TypeToken<PersistentData>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for (Nation nation : instance.getNations().values())
                nation.getPower().recalculate(nation);
        } else {
            file.getParentFile().mkdirs();

            instance = new PersistentData();
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> instance.save(file), NationVariables.instance.getSaveFrequency(), NationVariables.instance.getSaveFrequency());
    }

    public void save(File file) {
        Gson gson = new Gson();

        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(instance, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<UUID, Nation> getNations() {
        return nations;
    }

    public HashMap<UUID, PlayerData> getPlayers() {
        return players;
    }
}
