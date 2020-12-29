package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationPower;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandStatus extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationPower np = nation.getPower();

        sender.sendMessage(Language.format("nationStatus",
                new String[]{"nationPower", Integer.toString(np.getTotal())},
                new String[]{"nationClaimThreshold", Integer.toString(np.getClaimThreshold())},
                new String[]{"nationExistenceThreshold", Integer.toString(np.getExistenceThreshold())}
        ));
    }

    public String getName() {
        return "status";
    }

    public String getDescription() {
        return "Check your nation's power status.";
    }

    public String[] getAliases() {
        return new String[]{"status", "threshold", "thresholds"};
    }
}
