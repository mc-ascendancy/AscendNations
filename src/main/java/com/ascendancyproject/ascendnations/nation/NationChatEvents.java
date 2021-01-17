package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NationChatEvents implements Listener {
    public NationChatEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        PlayerData playerData = PersistentData.instance.getPlayers().get(event.getPlayer().getUniqueId());
        if (!playerData.isNationChat())
            return;

        event.setCancelled(true);

        if (playerData.getNationUUID() == null) {
            Language.sendMessage(event.getPlayer(), "errorNationChatNoNation");
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        nation.broadcast("nationChatFormat", new String[]{"message", event.getFormat().replace("%2$s", event.getMessage())});
    }
}
