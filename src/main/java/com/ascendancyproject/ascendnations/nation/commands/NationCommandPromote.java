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
        name = "promote",
        description = "Promote players to chancellor in your nation.",
        minimumRole = NationRole.Chancellor
)
public class NationCommandPromote extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            Language.sendMessage(player, "errorNationPromoteBadUsername");
            return;
        }

        OfflinePlayer promoted = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (promoted == null) {
            Language.sendMessage(player, "errorNationNoPlayerFound", new String[]{"playerName", args[1]});
            return;
        }

        if (promoted.getUniqueId() == player.getUniqueId()) {
            Language.sendMessage(player, "errorCannotRunOnYourself", new String[]{"playerName", args[1]});
            return;
        }

        NationMember promotedNationMember = nation.getMembers().get(promoted.getUniqueId());
        if (promotedNationMember == null) {
            Language.sendMessage(player, "errorNationPlayerNotInNation", new String[]{"playerName", args[1]});
            return;
        }

        if (promotedNationMember.getRole() == NationRole.Commander) {
            Language.sendMessage(player, "errorNationPromoteAlreadyCommander", new String[]{"playerName", args[1]});
            return;
        }

        promotedNationMember.setRole(NationRole.Commander);

        Language.sendMessage(player, "nationPromote", new String[]{"promotedName", args[1]});

        if (promoted.isOnline())
            Language.sendMessage((Player) promoted, "nationPromoteReceived", new String[]{"promoterName", player.getName()});

        nation.broadcast("nationPromoteBroadcast", new String[][]{
                new String[]{"nationName", nation.getName()},
                new String[]{"promoterName", player.getName()},
                new String[]{"promotedName", promoted.getName()}
        }, player.getUniqueId(), promoted.getUniqueId());
    }
}
