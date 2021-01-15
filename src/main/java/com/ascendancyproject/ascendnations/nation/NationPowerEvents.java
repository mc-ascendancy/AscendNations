package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NationPowerEvents implements Listener {
    public NationPowerEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerData killedPlayerData = PersistentData.instance.getPlayers().get(event.getEntity().getUniqueId());

        int killedPower = killedPlayerData.getPower().getTotal();
        killedPlayerData.getPower().subtractPower(event.getEntity());

        if (killedPlayerData.getNationUUID() != null) {
            Nation killedNation = PersistentData.instance.getNations().get(killedPlayerData.getNationUUID());
            killedNation.getPower().recalculate(killedNation);
        }

        if (event.getEntity().getKiller() != null) {
            PlayerData killerPlayerData = PersistentData.instance.getPlayers().get(event.getEntity().getKiller().getUniqueId());
            if (killerPlayerData.getNationUUID() == killedPlayerData.getNationUUID())
                return;

            if (killedPower < killerPlayerData.getPower().getTotal()) {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerLowerLevel", new String[]{"killedName", event.getEntity().getName()});
                return;
            }

            if (killerPlayerData.getPower().incrementBonusPower()) {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerGained",
                        new String[]{"killedName", event.getEntity().getName()},
                        new String[]{"bonusPower", Integer.toString(killedPlayerData.getPower().getBonusPower())},
                        new String[]{"bonusPowerMax", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
                );

                if (killerPlayerData.getNationUUID() != null) {
                    Nation killerNation = PersistentData.instance.getNations().get(killerPlayerData.getNationUUID());
                    killerNation.getPower().recalculate(killerNation);
                }
            } else {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerMaxLevel",
                        new String[]{"killedName", event.getEntity().getName()},
                        new String[]{"bonusPower", Integer.toString(killerPlayerData.getPower().getBonusPower())},
                        new String[]{"bonusPowerMax", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
                );
            }
        }
    }
}
