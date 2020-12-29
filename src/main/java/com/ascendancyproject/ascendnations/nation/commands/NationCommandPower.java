package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandPower extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationMember nationMember = nation.getMembers().get(player.getUniqueId());

        NationPower np = nation.getPower();
        NationMemberPower mp = nationMember.getPower();

        sender.sendMessage(Language.format("nationPower",
                new String[]{"nationPower", Integer.toString(np.getTotal())},
                new String[]{"maxNationPower", Integer.toString(np.getMaxPower())},
                new String[]{"memberPower", Integer.toString(np.getMemberPower())},
                new String[]{"maxMemberPower", Integer.toString(np.getMaxMemberPower())},
                new String[]{"riftPower", Integer.toString(np.getRiftPower())},
                new String[]{"maxRiftPower", Integer.toString(np.getMaxRiftPower())},
                new String[]{"playerPower", Integer.toString(mp.getTotal())},
                new String[]{"maxPlayerPower", Integer.toString(NationVariables.instance.getMaxMemberPower())},
                new String[]{"playerPassivePower", Integer.toString(mp.getPassivePower())},
                new String[]{"maxPlayerPassivePower", Integer.toString(NationVariables.instance.getMaxMemberPassivePower())},
                new String[]{"playerBonusPower", Integer.toString(mp.getBonusPower())},
                new String[]{"maxPlayerBonusPower", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
        ));
    }

    public String getName() {
        return "power";
    }

    public String getDescription() {
        return "Calculate your nation's power.";
    }

    public String[] getAliases() {
        return new String[]{"power"};
    }
}
