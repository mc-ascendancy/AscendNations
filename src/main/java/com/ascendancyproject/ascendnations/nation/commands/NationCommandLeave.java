package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandLeave extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationMember nationMember = nation.getMembers().get(player.getUniqueId());

        if (nationMember.getRole() == NationRole.Chancellor) {
            sender.sendMessage(Language.getLine("errorNationLeaveIsChancellor"));
            return;
        }

        playerData.setNationUUID(null);
        nation.getMembers().remove(player.getUniqueId());
        nation.getPower().recalculate(nation);

        sender.sendMessage(Language.format("nationLeave", new String[]{"nationName", nation.getName()}));
    }

    public String getName() {
        return "leave";
    }

    public String getDescription() {
        return "Leave your current nation.";
    }

    public String[] getAliases() {
        return new String[]{"leave"};
    }
}
