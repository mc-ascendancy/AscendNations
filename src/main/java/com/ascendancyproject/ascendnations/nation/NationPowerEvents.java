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
        if (killedPlayerData.getNationUUID() == null)
            return;

        Nation killedNation = PersistentData.instance.getNations().get(killedPlayerData.getNationUUID());
        NationMember killedMember = killedNation.getMembers().get(event.getEntity().getUniqueId());

        int killedPower = killedMember.getPower().getTotal();
        killedMember.getPower().subtractPower(event.getEntity());
        killedNation.getPower().recalculate(killedNation);

        if (event.getEntity().getKiller() != null) {
            PlayerData killerPlayerData = PersistentData.instance.getPlayers().get(event.getEntity().getKiller().getUniqueId());
            if (killerPlayerData.getNationUUID() == null || killerPlayerData.getNationUUID() == killedNation.getUUID())
                return;

            Nation killerNation = PersistentData.instance.getNations().get(killerPlayerData.getNationUUID());
            NationMember killerMember = killerNation.getMembers().get(event.getEntity().getKiller().getUniqueId());

            if (killedPower < killerMember.getPower().getTotal()) {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerLowerLevel", new String[]{"killedName", event.getEntity().getName()});
                return;
            }

            if (killerMember.getPower().incrementBonusPower()) {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerGained",
                        new String[]{"killedName", event.getEntity().getName()},
                        new String[]{"bonusPower", Integer.toString(killerMember.getPower().getBonusPower())},
                        new String[]{"bonusPowerMax", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
                );

                killerNation.getPower().recalculate(killerNation);
            } else {
                Language.sendMessage(event.getEntity().getKiller(), "nationBonusPowerMaxLevel",
                        new String[]{"killedName", event.getEntity().getName()},
                        new String[]{"bonusPower", Integer.toString(killerMember.getPower().getBonusPower())},
                        new String[]{"bonusPowerMax", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
                );
            }
        }
    }
}
