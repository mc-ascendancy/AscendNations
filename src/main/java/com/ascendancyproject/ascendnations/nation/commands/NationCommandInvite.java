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

import java.util.UUID;

@NationCommandAnnotation(
        name = "invite",
        description = "Invite your friends to your nation!",
        aliases = {"invite"},
        minimumRole = NationRole.Commander
)
public class NationCommandInvite extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationInviteBadUsername"));
            return;
        }

        if (!nation.hasMemberSlots()) {
            player.sendMessage(Language.format("errorNationHasNoMemberSlots", new String[]{"nationName", nation.getName()}));
            return;
        }

        Player invitee = Bukkit.getPlayer(args[1]);
        if (invitee == null) {
            player.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        PlayerData inviteePlayerData = PersistentData.instance.getPlayers().get(invitee.getUniqueId());
        if (inviteePlayerData.getNationUUID() != null) {
            player.sendMessage(Language.format("errorNationInviteInviteeInNation", new String[]{"inviteeName", args[1]}));
            return;
        }

        NationInvitation invitation = new NationInvitation(player.getUniqueId(), nation.getUUID());
        UUID invitationUUID = NationInvitationManager.createInvitation(invitation);

        player.sendMessage(Language.format("nationInvite", new String[]{"inviteeName", args[1]}));

        invitee.sendMessage(Language.format("nationInviteInvited", new String[]{"inviterName", player.getName()}, new String[]{"nationName", nation.getName()}));

        TextComponent textComponent = new TextComponent(Language.getLine("nationInviteInvitedClickable"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation join " + invitationUUID.toString()));
        invitee.sendMessage(textComponent);
    }
}
