package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NationCommandAnnotation(
        name = "invite",
        description = "Invite your friends to your nation!",
        minimumRole = NationRole.Commander
)
public class NationCommandInvite extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length != 2) {
            Language.sendMessage(player, "errorNationInviteBadUsername");
            return;
        }

        if (!nation.hasMemberSlots()) {
            Language.sendMessage(player, "errorNationHasNoMemberSlots", new String[]{"nationName", nation.getName()});
            return;
        }

        Player invitee = Bukkit.getPlayer(args[1]);
        if (invitee == null) {
            Language.sendMessage(player, "errorNationNoPlayerFound", new String[]{"playerName", args[1]});
            return;
        }

        PlayerData inviteePlayerData = PersistentData.instance.getPlayers().get(invitee.getUniqueId());
        if (inviteePlayerData.getNationUUID() != null) {
            Language.sendMessage(player, "errorNationInviteInviteeInNation", new String[]{"inviteeName", args[1]});
            return;
        }

        NationInvitation invitation = new NationInvitation(player.getUniqueId(), nation.getUUID());
        UUID invitationUUID = NationInvitationManager.createInvitation(invitation);

        Language.sendMessage(player, "nationInvite", new String[]{"inviteeName", args[1]});

        Language.sendMessage(invitee, "nationInviteInvited", new String[]{"inviterName", player.getName()}, new String[]{"nationName", nation.getName()});

        TextComponent textComponent = new TextComponent(Language.format(invitee, "nationInviteInvitedClickable"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation join " + invitationUUID.toString()));
        invitee.sendMessage(textComponent);
    }
}
