package com.ascendancyproject.ascendnations.events;

import org.bukkit.Bukkit;

public class NationScriptEvent {
    private static final String prefix = "discord";

    public NationScriptEvent(NationEventType type, String... args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("%s %s %s", prefix, type.toString(), String.join(" ", args)));
    }
}
