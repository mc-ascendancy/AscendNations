package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.CommandNation;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendNations extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialise languages.
        Language.init(new File(getDataFolder(), Language.location), this);

        // Initialise persistent data.
        PersistentData.init(new File(getDataFolder(), PersistentData.location), this);

        // Register commands.
        this.getCommand("nation").setExecutor(new CommandNation());
    }

    @Override
    public void onDisable() {
        // Save persistent data before exiting.
        PersistentData.instance.save(new File(getDataFolder(), PersistentData.location));
    }
}
