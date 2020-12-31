package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NationCommandJoin extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationJoinManual"));
            return;
        }

        if (nation != null) {
            player.sendMessage(Language.getLine("errorNationJoinAlreadyInNation"));
            return;
        }

        UUID invitationUUID = UUID.fromString(args[1]);
        NationInvitation invitation = NationInvitationManager.invitations.get(invitationUUID);

        if (invitation == null || invitation.hasExpired()) {
            player.sendMessage(Language.getLine("errorNationJoinExpired"));
            return;
        }

        nation = PersistentData.instance.getNations().get(invitation.getNationUUID());

        if (!nation.hasMemberSlots()) {
            player.sendMessage(Language.format("errorNationHasNoMemberSlots", new String[]{"nationName", nation.getName()}));
            return;
        }

        playerData.setNationUUID(nation.getUUID());
        nation.getMembers().put(player.getUniqueId(), new NationMember(NationRole.Citizen));
        nation.getPower().recalculate(nation);

        NationInvitationManager.invitations.remove(invitationUUID);

        player.sendMessage(Language.format("nationJoin", new String[]{"nationName", nation.getName()}));
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

    @Override
    public boolean requiresNation() {
        return false;
    }
}
