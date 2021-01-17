package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class NationPassivePowerTicker {
    private static final int tickFrequency = 10 * 20;

    public NationPassivePowerTicker(AscendNations plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, tickFrequency, tickFrequency);
    }

    private void tick() {
        for (Map.Entry<UUID, PlayerData> entry : PersistentData.instance.getPlayers().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player != null && player.isOnline())
                onlineTick(entry, player);
            else
                offlineTick(entry);
        }
    }

    private void onlineTick(Map.Entry<UUID, PlayerData> entry, Player player) {
        if (AscendNationsHelper.playerIsAdmin(player))
            return;

        if (!entry.getValue().getPower().gainPassivePower(tickFrequency))
            return;

        Language.sendMessage(player, "nationPassivePowerIncremented",
                new String[]{"totalPower", Integer.toString(entry.getValue().getPower().getTotal())},
                new String[]{"maxTotalPower", Integer.toString(NationVariables.instance.getMaxMemberPower())},
                new String[]{"passivePower", Integer.toString(entry.getValue().getPower().getPassivePower())},
                new String[]{"maxPassivePower", Integer.toString(NationVariables.instance.getMaxMemberPassivePower())}
        );

        if (entry.getValue().getNationUUID() != null) {
            Nation nation = PersistentData.instance.getNations().get(entry.getValue().getNationUUID());
            nation.getPower().recalculate(nation);
        }
    }

    private void offlineTick(Map.Entry<UUID, PlayerData> entry) {
        if (!entry.getValue().getPower().decayPassivePower(tickFrequency))
            return;

        if (entry.getValue().getNationUUID() != null) {
            Nation nation = PersistentData.instance.getNations().get(entry.getValue().getNationUUID());
            nation.getPower().recalculate(nation);
        }
    }
}
