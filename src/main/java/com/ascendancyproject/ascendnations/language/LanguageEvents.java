package com.ascendancyproject.ascendnations.language;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

public class LanguageEvents implements Listener {
    public LanguageEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLocaleChange(PlayerLocaleChangeEvent event) {
        PlayerData playerData = PersistentData.instance.getPlayers().get(event.getPlayer().getUniqueId());
        if (playerData.isLanguageDefined())
            return;

        String countryCode = event.getLocale().substring(0, 2);
        if (playerData.getLanguage().equals(countryCode) || !Language.config.getLanguages().containsKey(countryCode))
            return;

        playerData.setLanguage(countryCode);
        Language.sendMessage(event.getPlayer(), "languageUpdated", new String[]{"language", Language.config.getLanguages().get(countryCode).getName()});
    }
}
