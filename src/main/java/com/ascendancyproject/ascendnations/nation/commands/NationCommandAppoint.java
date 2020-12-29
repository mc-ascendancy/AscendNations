package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationCommandAppoint extends NationCommand {
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Language.getLine("errorNationAppointBadUsername"));
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (playerData.getNationUUID() == null) {
            sender.sendMessage(Language.getLine("errorNationNotInNation"));
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationMember nationMember = nation.getMembers().get(player.getUniqueId());

        if (nation.lacksPermissions(player.getUniqueId(), NationRole.Chancellor)) {
            sender.sendMessage(Language.format("errorNationBadPermissions", new String[]{"minimumRole", NationRole.Chancellor.name()}));
            return;
        }

        OfflinePlayer appointed = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (appointed == null) {
            sender.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (appointed.getUniqueId() == player.getUniqueId()) {
            sender.sendMessage(Language.format("errorCannotRunOnYourself", new String[]{"playerName", args[1]}));
            return;
        }

        NationMember appointedNationMember = nation.getMembers().get(appointed.getUniqueId());
        if (appointedNationMember == null) {
            sender.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        nationMember.setRole(NationRole.Commander);
        appointedNationMember.setRole(NationRole.Chancellor);

        sender.sendMessage(Language.format("nationAppoint", new String[]{"appointedName", args[1]}));

        if (appointed.isOnline())
            ((Player) appointed).sendMessage(Language.format("nationAppointReceived", new String[]{"appointerName", player.getName()}));
    }

    public String getName() {
        return "appoint";
    }

    public String getDescription() {
        return "Appoint a new Chancellor for your nation.";
    }

    public String[] getAliases() {
        return new String[]{"appoint"};
    }
}
