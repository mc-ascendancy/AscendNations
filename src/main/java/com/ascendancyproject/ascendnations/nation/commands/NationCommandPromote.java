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

@NationCommandAnnotation(
        name = "promote",
        description = "Promote players to chancellor in your nation.",
        aliases = {"promote"},
        minimumRole = NationRole.Chancellor
)
public class NationCommandPromote extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationPromoteBadUsername"));
            return;
        }

        OfflinePlayer promoted = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (promoted == null) {
            player.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (promoted.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Language.format("errorCannotRunOnYourself", new String[]{"playerName", args[1]}));
            return;
        }

        NationMember promotedNationMember = nation.getMembers().get(promoted.getUniqueId());
        if (promotedNationMember == null) {
            player.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        if (promotedNationMember.getRole() == NationRole.Commander) {
            player.sendMessage(Language.format("errorNationPromoteAlreadyCommander", new String[]{"playerName", args[1]}));
            return;
        }

        promotedNationMember.setRole(NationRole.Commander);

        player.sendMessage(Language.format("nationPromote", new String[]{"promotedName", args[1]}));

        if (promoted.isOnline())
            ((Player) promoted).sendMessage(Language.format("nationPromoteReceived", new String[]{"promoterName", player.getName()}));
    }
}
