package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
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
        name = "kick",
        description = "Kick players from your nation.",
        aliases = {"kick"},
        minimumRole = NationRole.Chancellor
)
public class NationCommandKick extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationKickBadUsername"));
            return;
        }

        OfflinePlayer kicked = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (kicked == null) {
            player.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (kicked.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Language.getLine("errorCannotRunOnYourself"));
            return;
        }

        PlayerData kickedPlayerData = PersistentData.instance.getPlayers().get(kicked.getUniqueId());
        if (!kickedPlayerData.getNationUUID().equals(nation.getUUID())) {
            player.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        kickedPlayerData.setNationUUID(null);
        nation.getMembers().remove(kicked.getUniqueId());
        nation.getPower().recalculate(nation);

        player.sendMessage(Language.format("nationKick", new String[]{"kickedName", args[1]}, new String[]{"nationName", nation.getName()}));

        if (kicked.isOnline())
            ((Player) kicked).sendMessage(Language.format("nationKickReceived", new String[]{"kickerName", player.getName()}, new String[]{"nationName", nation.getName()}));

        nation.broadcast(Language.format("nationKickBroadcast",
                new String[]{"nationName", nation.getName()},
                new String[]{"kickerName", player.getName()},
                new String[]{"kickedName", kicked.getName()}
        ), player.getUniqueId(), kicked.getUniqueId());
    }
}
