package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "demote",
        description = "Demote players from commander in your nation.",
        aliases = {"demote"},
        minimumRole = NationRole.Chancellor
)
public class NationCommandDemote extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationDemoteBadUsername"));
            return;
        }

        OfflinePlayer demoted = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (demoted == null) {
            player.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (demoted.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Language.getLine("errorCannotRunOnYourself"));
            return;
        }

        NationMember demotedNationMember = nation.getMembers().get(demoted.getUniqueId());
        if (demotedNationMember == null) {
            player.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        if (demotedNationMember.getRole() == NationRole.Citizen) {
            player.sendMessage(Language.format("errorNationDemoteAlreadyCitizen", new String[]{"playerName", args[1]}));
            return;
        }

        demotedNationMember.setRole(NationRole.Citizen);

        player.sendMessage(Language.format("nationDemote", new String[]{"demotedName", args[1]}));

        if (demoted.isOnline())
            ((Player) demoted).sendMessage(Language.format("nationDemoteReceived", new String[]{"demoterName", player.getName()}));
    }
}
