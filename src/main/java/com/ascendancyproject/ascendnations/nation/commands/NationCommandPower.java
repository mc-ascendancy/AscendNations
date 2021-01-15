package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "power",
        description = "Calculate your nation's power.",
        requiresNation = false
)
public class NationCommandPower extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        NationMemberPower mp = playerData.getPower();

        if (nation == null) {
            Language.sendMessage(player, "nationPowerMember",
                    new String[]{"playerPower", Integer.toString(mp.getTotal())},
                    new String[]{"maxPlayerPower", Integer.toString(NationVariables.instance.getMaxMemberPower())},
                    new String[]{"playerPassivePower", Integer.toString(mp.getPassivePower())},
                    new String[]{"maxPlayerPassivePower", Integer.toString(NationVariables.instance.getMaxMemberPassivePower())},
                    new String[]{"playerBonusPower", Integer.toString(mp.getBonusPower())},
                    new String[]{"maxPlayerBonusPower", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
            );
        } else {
            NationPower np = nation.getPower();
            Language.sendMessage(player, "nationPower",
                    new String[]{"nationPower", Integer.toString(np.getTotal())},
                    new String[]{"maxNationPower", Integer.toString(np.getMaxPower())},
                    new String[]{"memberPower", Integer.toString(np.getMemberPower())},
                    new String[]{"maxMemberPower", Integer.toString(np.getMaxMemberPower())},
                    new String[]{"riftPower", Integer.toString(np.getRiftPower())},
                    new String[]{"maxRiftPower", Integer.toString(np.getMaxRiftPower())},
                    new String[]{"playerPower", Integer.toString(mp.getTotal())},
                    new String[]{"maxPlayerPower", Integer.toString(NationVariables.instance.getMaxMemberPower())},
                    new String[]{"playerPassivePower", Integer.toString(mp.getPassivePower())},
                    new String[]{"maxPlayerPassivePower", Integer.toString(NationVariables.instance.getMaxMemberPassivePower())},
                    new String[]{"playerBonusPower", Integer.toString(mp.getBonusPower())},
                    new String[]{"maxPlayerBonusPower", Integer.toString(NationVariables.instance.getMaxMemberBonusPower())}
            );
        }
    }
}
