package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class NationCommand {
    public void register(HashMap<String, NationCommand> commandMap) {
        for (String alias : getAliases())
            commandMap.put(alias, this);
    }

    abstract public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args);

    abstract public String getName();
    abstract public String getDescription();
    abstract public String[] getAliases();

    public boolean requiresNation() {
        return true;
    }

    public NationRole minimumRole() {
        return NationRole.Citizen;
    }
}
