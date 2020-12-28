package com.ascendancyproject.ascendnations;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class NationCommand {
    public void register(HashMap<String, NationCommand> commandMap) {
        for (String alias : getAliases())
            commandMap.put(alias, this);
    }

    abstract public void execute(CommandSender sender, Command command, String label, String[] args);

    abstract public String getName();
    abstract public String getDescription();
    abstract public String[] getAliases();
}
