package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.entity.Player;

public class NationMemberPower {
    private int passivePower;
    private int bonusPower;
    private int ticksUntilPassiveGain;
    private int ticksUntilPassiveDecay;

    public NationMemberPower() {
        passivePower = NationVariables.instance.getMemberPassivePowerStart();
        bonusPower = 0;
        ticksUntilPassiveGain = NationVariables.instance.getMemberPassiveGainFrequency();
        ticksUntilPassiveDecay = NationVariables.instance.getMemberPassiveDecayFrequency();
    }

    public boolean gainPassivePower(int ticksSinceLastCheck) {
        ticksUntilPassiveGain -= ticksSinceLastCheck;

        if (ticksUntilPassiveGain > 0)
            return false;

        ticksUntilPassiveGain += NationVariables.instance.getMemberPassiveGainFrequency();

        if (passivePower >= NationVariables.instance.getMaxMemberPassivePower())
            return false;

        passivePower++;
        return true;
    }

    public boolean decayPassivePower(int ticksSinceLastCheck) {
        ticksUntilPassiveDecay -= ticksSinceLastCheck;

        if (ticksUntilPassiveDecay > 0)
            return false;

        ticksUntilPassiveDecay += NationVariables.instance.getMemberPassiveDecayFrequency();

        if (passivePower <= 0)
            return false;

        passivePower--;
        return true;
    }

    public void resetPassivePowerTicker() {
        ticksUntilPassiveGain = NationVariables.instance.getMemberPassiveGainFrequency();
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

        Language.sendMessage(player, "nationPowerLost",
                new String[]{"bonusPowerLost", Integer.toString(bonusPowerLost)},
                new String[]{"passivePowerLost", Integer.toString(loss)}
        );
    }

    public int getTotal() {
        return passivePower + bonusPower;
    }

    public int getPassivePower() {
        return passivePower;
    }

    public void subtractPassivePower(int lost) {
        passivePower -= lost;
    }

    public int getBonusPower() {
        return bonusPower;
    }
}
