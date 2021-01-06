package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.claim.*;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.language.LanguageCommand;
import com.ascendancyproject.ascendnations.language.LanguageEvents;
import com.ascendancyproject.ascendnations.language.LanguageTabCompleter;
import com.ascendancyproject.ascendnations.nation.*;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendNations extends JavaPlugin {
    private static AscendNations instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load configs.
        NationVariables.init(new File(getDataFolder(), NationVariables.location), this);
        RiftConfig.init(new File(getDataFolder(), RiftConfig.location), this);

        // Initialise managers.
        NationInvitationManager.init(this);
        new NationPassivePowerTicker(this);
        new OverclaimTicker(this);
        new NationOutpostTicker(this);
        new NationLockoutTicker(this);

        // Register commands.
        this.getCommand("nation").setExecutor(new CommandNation());
        this.getCommand("nation").setTabCompleter(new NationTabCompleter());
        this.getCommand("language").setExecutor(new LanguageCommand());
        this.getCommand("language").setTabCompleter(new LanguageTabCompleter());

        // Load language config (must be loaded after CommandNation).
        Language.init(new File(getDataFolder(), Language.location), this);

        // Initialise persistent data.
        PersistentData.init(new File(getDataFolder(), PersistentData.location), this);
        ClaimChunks.init();

        // Register events.
        new PlayerDataEvents(this);
        new ClaimBlockEvents(this);
        new ClaimProtectionEvents(this);
        new OverclaimEvents(this);
        new NationOutpostEvents(this);
        new NationPowerEvents(this);
        new NationSpawnEvents(this);
        new NationClaimEvents(this);
        new ClaimChunksEvents(this);
        new LanguageEvents(this);
    }

    @Override
    public void onDisable() {
        // Save persistent data before exiting.
        PersistentData.instance.save(new File(getDataFolder(), PersistentData.location));
    }

    public static AscendNations getInstance() {
        return instance;
    }
}
