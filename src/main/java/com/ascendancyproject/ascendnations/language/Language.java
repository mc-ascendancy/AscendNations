package com.ascendancyproject.ascendnations.language;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Language {
    public static final String location = "languages.json";

    public static LanguageConfig config;

    public static void init(File file, AscendNations plugin) {
        if (!file.exists())
            plugin.saveResource(location, false);

        Gson gson = new Gson();

        try {
            config = gson.fromJson(new FileReader(file), new TypeToken<LanguageConfig>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (LanguageLang language : config.getLanguages().values()) {
            for (Map.Entry<String, String> entry : language.getLines().entrySet())
                entry.setValue(ChatColor.translateAlternateColorCodes('&', entry.getValue()));

            HashMap<String, String> commandsReverse = new HashMap<>();

            for (Map.Entry<String, String> entry : language.getCommands().entrySet())
                commandsReverse.put(entry.getValue(), entry.getKey());

            language.setCommandsReverse(commandsReverse);
        }
    }

    public static void sendMessage(Player player, String key, String[]... replacements) {
        player.sendMessage(format(player, key, replacements));
    }

    public static void broadcastMessage(String key, String[]... replacements) {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            Language.format(player, key, replacements);
    }

    public static void broadcastMessage(String key, String[][] replacements, UUID... exceptions) {
        HashSet<UUID> exceptionSet = new HashSet<>(Arrays.asList(exceptions));

        for (Player player : Bukkit.getServer().getOnlinePlayers())
            if (!exceptionSet.contains(player.getUniqueId()))
                Language.format(player, key, replacements);
    }

    public static String format(Player player, String key, String[]... replacements) {
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());
        return formatLanguage(getLanguage(playerData), key, replacements);
    }

    public static String formatDefault(String key, String[]... replacements) {
        return formatLanguage(config.getLanguages().get(config.getDefaultLanguage()), key, replacements);
    }

    private static String formatLanguage(LanguageLang language, String key, String[]... replacements) {
        String msg = language.getLines().get(key);
        if (msg == null)
            msg = config.getLanguages().get(config.getDefaultLanguage()).getLines().get(key);

        String prefix = language.getLines().get("prefix");
        if (prefix == null)
            prefix = config.getLanguages().get(config.getDefaultLanguage()).getLines().get("prefix");

        msg = msg.replace("[prefix]", prefix);

        for (String[] replacement : replacements)
            msg = msg.replace("[" + replacement[0] + "]", replacement[1]);

        return msg;
    }

    public static LanguageLang getLanguage(PlayerData playerData) {
        String language = playerData.getLanguage();
        if (!config.getLanguages().containsKey(language)) {
            language = config.getDefaultLanguage();
            playerData.setLanguage(language);
        }

        return config.getLanguages().get(language);
    }
}
