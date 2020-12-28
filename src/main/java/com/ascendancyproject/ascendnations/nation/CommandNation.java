package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandNation implements CommandExecutor {
    private final HashMap<String, NationCommand> commandMap;

    public CommandNation() {
        commandMap = new HashMap<>();

        // Register subcommands.
        new NationCommandCreate().register(commandMap);
        new NationCommandDisband().register(commandMap);
        new NationCommandInvite().register(commandMap);
        new NationCommandJoin().register(commandMap);
        new NationCommandLeave().register(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: update this in accordance with issue #4.
            sender.sendMessage(Language.format("errorNotPlayer", new String[]{"commandName", label}));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Language.format("errorNoSubcommandProvided", new String[]{"subcommandName", label}));
            return true;
        }

        NationCommand nationCommand = commandMap.get(args[0]);
        if (nationCommand == null) {
            sender.sendMessage(Language.format("errorBadSubcommand", new String[]{"subcommandName", label}));
            return true;
        }

        nationCommand.execute(sender, command, label, args);
        return true;
    }
}
