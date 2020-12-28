package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandCreate extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            // TODO: message player error reason.
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData == null) {
            playerData = new PlayerData();
            PersistentData.instance.getPlayers().put(player.getUniqueId(), playerData);
        }

        if (playerData.getNationUUID() != null) {
            // TODO: message player error reason.
            return;
        }

        Nation nation = new Nation(player, String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length)));
        playerData.setNationUUID(nation.getUUID());
    }

    public String getName() {
        return "create";
    }

    public String getDescription() {
        return "Form a new nation.";
    }

    public String[] getAliases() {
        return new String[]{"create", "form"};
    }
}
