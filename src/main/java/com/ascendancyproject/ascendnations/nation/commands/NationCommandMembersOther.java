package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NationCommandAnnotation(
        name = "membersother",
        description = "View the members of another nation.",
        requiresNation = false,
        hidden = true
)
public class NationCommandMembersOther extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorAutomaticCommandManual"));
            return;
        }

        UUID nationUUID;
        try {
            nationUUID = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Language.getLine("errorAutomaticCommandManual"));
            return;
        }

        Nation otherNation = PersistentData.instance.getNations().get(nationUUID);
        if (otherNation == null) {
            player.sendMessage(Language.getLine("errorNationMembersNotFound"));
            return;
        }

        otherNation.listMembers(player);
    }
}
