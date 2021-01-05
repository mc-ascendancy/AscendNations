package com.ascendancyproject.ascendnations.language;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LanguageTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1)
            return null;

        ArrayList<String> languages = new ArrayList<>();

        for (LanguageLang language : Language.config.getLanguages().values())
            languages.add(language.getName());

        return languages;
    }
}
