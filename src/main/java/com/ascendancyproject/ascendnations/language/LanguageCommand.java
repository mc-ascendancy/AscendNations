package com.ascendancyproject.ascendnations.language;

import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LanguageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.formatDefault("errorNotPlayer", new String[]{"commandName", label}));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            Language.sendMessage(player, "errorLanguageNoLanguage");
            return true;
        }

        String lang = null;
        for (Map.Entry<String, LanguageLang> entry : Language.config.getLanguages().entrySet()) {
            if (!entry.getValue().getName().equals(args[0].toLowerCase()))
                continue;

            lang = entry.getKey();
        }

        if (lang == null) {
            Language.sendMessage(player, "errorLanguageNotSupported", new String[]{"language", args[0]});
            return true;
        }

        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        if (lang.equals(playerData.getLanguage())) {
            Language.sendMessage(player, "errorLanguageSameLanguage", new String[]{"language", args[0]});
            return true;
        }

        playerData.setLanguage(lang);
        playerData.setLanguageDefined(true);

        Language.sendMessage(player, "languageManual", new String[]{"language", args[0]});
        return true;
    }
}
