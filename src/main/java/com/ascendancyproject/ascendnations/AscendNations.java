package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.nation.CommandNation;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendNations extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register commands.
        this.getCommand("nation").setExecutor(new CommandNation());

        File persistentDataFile = new File(getDataFolder(), PersistentData.location);
        PersistentData.init(persistentDataFile, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
