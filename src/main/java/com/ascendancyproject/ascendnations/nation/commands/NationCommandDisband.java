package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NationCommandAnnotation(
        name = "disband",
        description = "Disband your nation.",
        aliases = {"disband", "dissolve"},
        minimumRole = NationRole.Chancellor
)
public class NationCommandDisband extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        String name = nation.getName();
        nation.disband();

        player.sendMessage(Language.format("nationDisbanded", new String[]{"nationName", name}));
    }
}
