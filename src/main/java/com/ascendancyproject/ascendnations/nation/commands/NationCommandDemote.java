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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandDemote extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Language.getLine("errorNationDemoteBadUsername"));
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());

        if (nation.lacksPermissions(player.getUniqueId(), NationRole.Chancellor)) {
            sender.sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Chancellor.name()}));
            return;
        }

        OfflinePlayer demoted = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (demoted == null) {
            sender.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (demoted.getUniqueId() == player.getUniqueId()) {
            sender.sendMessage(Language.getLine("errorCannotRunOnYourself"));
            return;
        }

        NationMember demotedNationMember = nation.getMembers().get(demoted.getUniqueId());
        if (demotedNationMember == null) {
            sender.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        if (demotedNationMember.getRole() == NationRole.Citizen) {
            sender.sendMessage(Language.format("errorNationDemoteAlreadyCitizen", new String[]{"playerName", args[1]}));
            return;
        }

        demotedNationMember.setRole(NationRole.Citizen);

        sender.sendMessage(Language.format("nationDemote", new String[]{"demotedName", args[1]}));

        if (demoted.isOnline())
            ((Player) demoted).sendMessage(Language.format("nationDemoteReceived", new String[]{"demoterName", player.getName()}));
    }

    public String getName() {
        return "demote";
    }

    public String getDescription() {
        return "Demote players from chancellor in your nation.";
    }

    public String[] getAliases() {
        return new String[]{"demote"};
    }
}
