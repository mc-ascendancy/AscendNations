package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
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

public class NationCommandKick extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
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
    }

    public String getName() {
        return "kick";
    }

    public String getDescription() {
        return "Kick players from your nation.";
    }

    public String[] getAliases() {
        return new String[]{"kick"};
    }

    @Override
    public NationRole minimumRole() {
        return NationRole.Chancellor;
    }
}
