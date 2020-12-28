package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationInvitation;
import com.ascendancyproject.ascendnations.nation.NationInvitationManager;
import com.ascendancyproject.ascendnations.nation.NationRole;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NationCommandInvite extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Language.getLine("errorNationInviteBadUsername"));
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        if (nation == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        if (nation.lacksPermissions(player.getUniqueId(), NationRole.Commander)) {
            sender.sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Commander.name()}));
            return;
        }

        Player invitee = Bukkit.getPlayer(args[1]);
        if (invitee == null) {
            sender.sendMessage(Language.format("errorNationInviteNoInviteeFound", new String[]{"inviteeName", args[1]}));
            return;
        }

        PlayerData inviteePlayerData = PersistentData.instance.getPlayers().get(invitee.getUniqueId());
        if (inviteePlayerData.getNationUUID() != null) {
            sender.sendMessage(Language.format("errorNationInviteInviteeInNation", new String[]{"inviteeName", args[1]}));
            return;
        }

        NationInvitation invitation = new NationInvitation(player.getUniqueId(), nation.getUUID());
        UUID invitationUUID = NationInvitationManager.createInvitation(invitation);

        sender.sendMessage(Language.format("nationInvite", new String[]{"inviteeName", args[1]}));

        invitee.sendMessage(Language.format("nationInviteInvited", new String[]{"inviterName", player.getName()}, new String[]{"nationName", nation.getName()}));

        TextComponent textComponent = new TextComponent(Language.getLine("nationInviteInvitedClickable"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation join " + invitationUUID.toString()));
        invitee.sendMessage(textComponent);
    }

    public String getName() {
        return "invite";
    }

    public String getDescription() {
        return "Invite your friends to your nation!";
    }

    public String[] getAliases() {
        return new String[]{"invite"};
    }
}
