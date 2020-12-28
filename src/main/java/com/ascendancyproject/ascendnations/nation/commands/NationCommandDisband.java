package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandDisband extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationDisbandNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());

        if (nation.lacksPermissions(player.getUniqueId(), NationRole.Chancellor)) {
            sender.sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Chancellor.name()}));
            return;
        }

        String name = nation.getName();
        nation.disband();

        sender.sendMessage(Language.format("nationDisbanded", new String[]{"nationName", name}));
    }

    public String getName() {
        return "disband";
    }

    public String getDescription() {
        return "Disband your nation.";
    }

    public String[] getAliases() {
        return new String[]{"disband", "dissolve"};
    }
}
