package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.claim.ClaimBlockEvents;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.CommandNation;
import com.ascendancyproject.ascendnations.nation.NationInvitationManager;
import com.ascendancyproject.ascendnations.nation.NationPassivePowerTicker;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendNations extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load configs.
        Language.init(new File(getDataFolder(), Language.location), this);
        NationVariables.init(new File(getDataFolder(), NationVariables.location), this);

        // Initialise persistent data.
        PersistentData.init(new File(getDataFolder(), PersistentData.location), this);

        // Initialise managers.
        NationInvitationManager.init(this);
        new NationPassivePowerTicker(this);

        // Register commands.
        this.getCommand("nation").setExecutor(new CommandNation());

        // Register events.
        new PlayerDataEvents(this);
        new ClaimBlockEvents(this);
    }

    @Override
    public void onDisable() {
        // Save persistent data before exiting.
        PersistentData.instance.save(new File(getDataFolder(), PersistentData.location));
    }
}
