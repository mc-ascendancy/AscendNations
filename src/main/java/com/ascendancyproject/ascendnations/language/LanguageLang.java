package com.ascendancyproject.ascendnations.language;

import java.util.HashMap;

public class LanguageLang {
    private String name;
    private HashMap<String, String> lines;

    private HashMap<String, String> commands;
    private HashMap<String, String> commandsReverse;

    public String getName() {
        return name;
    }

    public HashMap<String, String> getLines() {
        return lines;
    }

    public HashMap<String, String> getCommands() {
        return commands;
    }

    public HashMap<String, String> getCommandsReverse() {
        return commandsReverse;
    }

    public void setCommandsReverse(HashMap<String, String> commandsReverse) {
        this.commandsReverse = commandsReverse;
    }
}
