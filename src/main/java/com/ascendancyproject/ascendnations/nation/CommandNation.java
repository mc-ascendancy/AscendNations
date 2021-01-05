package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.language.LanguageLang;
import com.ascendancyproject.ascendnations.nation.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandNation implements CommandExecutor {
    public static HashMap<String, NationCommand> commandMap;

    public CommandNation() {
        commandMap = new HashMap<>();

        // Register subcommands.
        new NationCommandCreate().register(commandMap);
        new NationCommandDisband().register(commandMap);
        new NationCommandInvite().register(commandMap);
        new NationCommandJoin().register(commandMap);
        new NationCommandLeave().register(commandMap);
        new NationCommandPromote().register(commandMap);
        new NationCommandDemote().register(commandMap);
        new NationCommandKick().register(commandMap);
        new NationCommandAppoint().register(commandMap);
        new NationCommandPower().register(commandMap);
        new NationCommandStatus().register(commandMap);
        new NationCommandClaim().register(commandMap);
        new NationCommandUnclaim().register(commandMap);
        new NationCommandOutposts().register(commandMap);
        new NationCommandHelp().register(commandMap);
        new NationCommandMessage().register(commandMap);
        new NationCommandMembers().register(commandMap);
        new NationCommandMembersOther().register(commandMap);
        new NationCommandList().register(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: update this in accordance with issue #4.
            sender.sendMessage(Language.formatDefault("errorNotPlayer", new String[]{"commandName", label}));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Language.sendMessage(player, "errorNoSubcommandProvided", new String[]{"subcommandName", label});
            return true;
        }

        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());
        LanguageLang lang = Language.getLanguage(playerData);

        String commandTranslated = lang.getCommandsReverse().get(args[0].toLowerCase());
        if (commandTranslated == null) {
            Language.sendMessage(player, "errorBadSubcommand", new String[]{"subcommandName", label});
            return true;
        }

        NationCommand nationCommand = commandMap.get(commandTranslated);
        NationCommandAnnotation annotation = nationCommand.getAnnotation();

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        if (nation == null && annotation.requiresNation()) {
            Language.sendMessage(player, "errorNationNotInNation");
            return true;
        }

        NationMember member = null;

        if (nation != null) {
            if (nation.lacksPermissions(player.getUniqueId(), annotation.minimumRole())) {
                Language.sendMessage(player, "errorNationBadPermissions", new String[]{"minimumRole", annotation.minimumRole().name()});
                return true;
            }

            member = nation.getMembers().get(player.getUniqueId());
        }

        nationCommand.execute(player, playerData, nation, member, args);
        return true;
    }
}
