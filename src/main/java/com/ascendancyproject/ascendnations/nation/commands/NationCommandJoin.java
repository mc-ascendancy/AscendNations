package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NationCommandJoin extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Language.getLine("errorNationJoinManual"));
            return;
        }

        UUID invitationUUID = UUID.fromString(args[1]);
        NationInvitation invitation = NationInvitationManager.invitations.get(invitationUUID);

        if (invitation == null || invitation.hasExpired()) {
            sender.sendMessage(Language.getLine("errorNationJoinExpired"));
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());
        Nation nation = PersistentData.instance.getNations().get(invitation.getNationUUID());

        playerData.setNationUUID(nation.getUUID());
        nation.getMembers().put(player.getUniqueId(), new NationMember(NationRole.Citizen));
        nation.getPower().recalculate(nation);

        NationInvitationManager.invitations.remove(invitationUUID);

        sender.sendMessage(Language.format("nationJoin", new String[]{"nationName", nation.getName()}));
    }

    public String getName() {
        return "join";
    }

    public String getDescription() {
        return "Join your friend's nation!";
    }

    public String[] getAliases() {
        return new String[]{"join"};
    }
}
