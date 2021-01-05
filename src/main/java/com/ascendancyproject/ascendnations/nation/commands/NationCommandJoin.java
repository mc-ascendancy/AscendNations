package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NationCommandAnnotation(
        name = "join",
        description = "Join your friends' nation!",
        requiresNation = false,
        requiresNoNation = true,
        hidden = true
)
public class NationCommandJoin extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            Language.sendMessage(player, "errorAutomaticCommandManual");
            return;
        }

        if (nation != null) {
            Language.sendMessage(player, "errorNationJoinAlreadyInNation");
            return;
        }

        UUID invitationUUID;
        try {
            invitationUUID = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            Language.sendMessage(player, "errorAutomaticCommandManual");
            return;
        }

        NationInvitation invitation = NationInvitationManager.invitations.get(invitationUUID);

        if (invitation == null || invitation.hasExpired()) {
            Language.sendMessage(player, "errorNationJoinExpired");
            return;
        }

        nation = PersistentData.instance.getNations().get(invitation.getNationUUID());

        if (!nation.hasMemberSlots()) {
            Language.sendMessage(player, "errorNationHasNoMemberSlots", new String[]{"nationName", nation.getName()});
            return;
        }

        playerData.setNationUUID(nation.getUUID());
        nation.getMembers().put(player.getUniqueId(), new NationMember(NationRole.Citizen, player.getUniqueId(), nation));
        nation.getPower().recalculate(nation);

        NationInvitationManager.invitations.remove(invitationUUID);

        Language.sendMessage(player, "nationJoin", new String[]{"nationName", nation.getName()});
        nation.broadcast("nationJoinBroadcast", new String[][]{new String[]{"nationName", nation.getName()}, new String[]{"playerName", player.getName()}}, player.getUniqueId());
    }
}
