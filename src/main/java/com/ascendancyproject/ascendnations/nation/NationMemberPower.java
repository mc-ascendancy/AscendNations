package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.entity.Player;

public class NationMemberPower {
    private int passivePower;
    private int bonusPower;
    private int ticksUntilPassiveGain;

    public NationMemberPower() {
        passivePower = 0;
        bonusPower = 0;
        ticksUntilPassiveGain = NationVariables.instance.getMemberPassiveGainFrequency();
    }

    public boolean updatePassivePower(int ticksSinceLastCheck) {
        ticksUntilPassiveGain -= ticksSinceLastCheck;

        if (ticksUntilPassiveGain <= 0) {
            ticksUntilPassiveGain += NationVariables.instance.getMemberPassiveGainFrequency();

            if (passivePower >= NationVariables.instance.getMaxMemberPassivePower())
                return false;

            passivePower++;
            return true;
        }

        return false;
    }

    public boolean incrementBonusPower() {
        if (bonusPower >= NationVariables.instance.getMaxMemberBonusPower())
            return false;

        bonusPower++;
        return true;
    }

    public void subtractPower(Player player) {
        int loss = NationVariables.instance.getMemberPowerLostOnDeath();

        int bonusPowerLost = Math.min(bonusPower, loss);
        bonusPower -= bonusPowerLost;
        loss -= bonusPowerLost;

        passivePower = Math.max(passivePower - loss, 0);

        player.sendMessage(Language.format("nationPowerLost",
                new String[]{"bonusPowerLost", Integer.toString(bonusPowerLost)},
                new String[]{"passivePowerLost", Integer.toString(loss)}
        ));
    }

    public int getTotal() {
        return passivePower + bonusPower;
    }

    public int getPassivePower() {
        return passivePower;
    }

    public int getBonusPower() {
        return bonusPower;
    }
}
