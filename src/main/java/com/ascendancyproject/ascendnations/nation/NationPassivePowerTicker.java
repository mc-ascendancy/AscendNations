package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
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
        for (Nation nation : PersistentData.instance.getNations().values()) {
            boolean updated = false;

            for (Map.Entry<UUID, NationMember> entry : nation.getMembers().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());

                if (player == null || !player.isOnline())
                    continue;

                if (!entry.getValue().getPower().updatePassivePower(tickFrequency))
                    continue;

                NationMemberPower power = nation.getMembers().get(player.getUniqueId()).getPower();

                Language.sendMessage(player, "nationPassivePowerIncremented",
                        new String[]{"totalPower", Integer.toString(power.getTotal())},
                        new String[]{"maxTotalPower", Integer.toString(NationVariables.instance.getMaxMemberPower())},
                        new String[]{"passivePower", Integer.toString(power.getPassivePower())},
                        new String[]{"maxPassivePower", Integer.toString(NationVariables.instance.getMaxMemberPassivePower())}
                );

                updated = true;
            }

            if (updated)
                nation.getPower().recalculate(nation);
        }
    }
}
