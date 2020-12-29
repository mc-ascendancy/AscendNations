package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandKick extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Language.getLine("errorNationKickBadUsername"));
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        if (nation == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        if (nation.lacksPermissions(player.getUniqueId(), NationRole.Chancellor)) {
            sender.sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Chancellor.name()}));
            return;
        }

        OfflinePlayer kicked = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (kicked == null) {
            sender.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (kicked.getUniqueId() == player.getUniqueId()) {
            sender.sendMessage(Language.getLine("errorCannotRunOnYourself"));
            return;
        }

        PlayerData kickedPlayerData = PersistentData.instance.getPlayers().get(kicked.getUniqueId());
        if (kickedPlayerData.getNationUUID() != nation.getUUID()) {
            sender.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        kickedPlayerData.setNationUUID(null);
        nation.getMembers().remove(kicked.getUniqueId());

        sender.sendMessage(Language.format("nationKick", new String[]{"kickedName", args[1]}, new String[]{"nationName", nation.getName()}));

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
}
