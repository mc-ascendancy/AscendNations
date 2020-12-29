package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class NationVariables {
    public static final String location = "nationvariables.json";

    public static NationVariables instance;

    private int maxNationPop;
    private int maxMemberPassivePower;
    private int maxMemberBonusPower;
    private int riftModifier;
    private int minNationPopClaimRift;
    private int memberPassiveGainFrequency;
    private int memberBonusGainOnKill;
    private int memberPowerLostOnDeath;

    public static void init(File file, AscendNations plugin) {
        if (!file.exists())
            plugin.saveResource(location, false);

        Gson gson = new Gson();

        try {
            instance = gson.fromJson(new FileReader(file), new TypeToken<NationVariables>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getMaxNationPop() {
        return maxNationPop;
    }

    public int getMaxMemberPower() {
        return maxMemberPassivePower + maxMemberBonusPower;
    }

    public int getMaxMemberPassivePower() {
        return maxMemberPassivePower;
    }

    public int getMaxMemberBonusPower() {
        return maxMemberBonusPower;
    }

    public int getRiftModifier() {
        return riftModifier;
    }

    public int getMinNationPopClaimRift() {
        return minNationPopClaimRift;
    }

    public int getMemberPassiveGainFrequency() {
        return memberPassiveGainFrequency;
    }

    public int getMemberBonusGainOnKill() {
        return memberBonusGainOnKill;
    }

    public int getMemberPowerLostOnDeath() {
        return memberPowerLostOnDeath;
    }
}
