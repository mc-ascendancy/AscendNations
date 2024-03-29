package com.ascendancyproject.ascendnations.nation.commands;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "home",
        description = "Teleport to your nation's home."
)
public class NationCommandHome extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (nation.getHome() == null) {
            Language.sendMessage(player, "nationHomeNoHome");
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CombatLogX")) {
            ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
            ICombatManager combatManager = plugin.getCombatManager();

            if (combatManager.isInCombat(player)) {
                Language.sendMessage(player, "nationHomeInCombat");
                return;
            }
        }

        player.teleport(player.getWorld().getBlockAtKey(nation.getHome()).getLocation().add(0.5, 1, 0.5));

        int passivePowerLost = Math.min(NationVariables.instance.getHomePowerCost(), playerData.getPower().getPassivePower());
        playerData.getPower().subtractPassivePower(passivePowerLost);

        Language.sendMessage(player, "nationHome", new String[]{"passivePowerLost", Integer.toString(passivePowerLost)});

        if (passivePowerLost != NationVariables.instance.getHomePowerCost()) {
            playerData.getPower().resetPassivePowerTicker();
            Language.sendMessage(player, "nationHomePowerReset");
        }

        if (passivePowerLost != 0)
            nation.getPower().recalculate(nation);
    }
}
