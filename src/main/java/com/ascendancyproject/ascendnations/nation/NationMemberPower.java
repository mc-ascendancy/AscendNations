package com.ascendancyproject.ascendnations.nation;

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

    public void incrementBonusPower() {
        bonusPower = Math.min(bonusPower + NationVariables.instance.getMemberBonusGainOnKill(), NationVariables.instance.getMaxMemberBonusPower());
    }

    public void subtractPower() {
        int loss = NationVariables.instance.getMemberPowerLostOnDeath();

        int bonusPowerLost = Math.min(bonusPower, loss);
        bonusPower -= bonusPowerLost;
        loss -= bonusPowerLost;

        passivePower = Math.max(passivePower - loss, 0);
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
