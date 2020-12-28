package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandDisband extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData == null) {
            playerData = new PlayerData();
            PersistentData.instance.getPlayers().put(player.getUniqueId(), playerData);
        }

        if (playerData.getNationUUID() == null) {
            // TODO: message player error reason.
            return;
        }

        PersistentData.instance.getNations().get(playerData.getNationUUID()).disband();
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
