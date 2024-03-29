package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class NationCommand {
    public void register(HashMap<String, NationCommand> commandMap) {
        commandMap.put(getAnnotation().name(), this);
    }

    abstract public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args);

    public NationCommandAnnotation getAnnotation() {
        return this.getClass().getAnnotation(NationCommandAnnotation.class);
    }

    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        return null;
    }
}
