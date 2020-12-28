package com.ascendancyproject.ascendnations.language;

import com.ascendancyproject.ascendnations.AscendNations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Language {
    public static final String location = "languages.json";

    private static HashMap<String, String> lines;

    public static void init(File file, AscendNations plugin) {
        if (!file.exists())
            plugin.saveResource(location, false);

        Gson gson = new Gson();

        try {
            lines = gson.fromJson(new FileReader(file), new TypeToken<HashMap<String, String>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : lines.entrySet())
            entry.setValue(ChatColor.translateAlternateColorCodes('&', entry.getValue()));
    }

    public static String format(String key, String[]... replacements) {
        String msg = lines.get(key);

        for (String[] replacement : replacements)
            msg = msg.replace("[" + replacement[0] + "]", replacement[1]);

        return msg;
    }

    public static String getLine(String key) {
        return lines.get(key);
    }
}
