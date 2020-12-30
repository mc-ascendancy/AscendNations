package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

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

    private int claimThresholdOnePlayer;
    private int claimThresholdSmallModifier;
    private int claimThresholdSmallOffset;
    private int claimThresholdLargeModifier;
    private int existenceThresholdOnePlayer;
    private int existenceThresholdSmallModifier;
    private int existenceThresholdSmallOffset;
    private int existenceThresholdLargeModifierDynamic;
    private int existenceThresholdLargeModifierFlat;

    private float chunksPerMember;
    private float outpostsPerMember;

    private String claimBlockHomeType;
    private Material claimBlockHome;

    public static void init(File file, AscendNations plugin) {
        if (!file.exists())
            plugin.saveResource(location, false);

        Gson gson = new Gson();

        try {
            instance = gson.fromJson(new FileReader(file), new TypeToken<NationVariables>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        instance.claimBlockHome = Material.getMaterial(instance.claimBlockHomeType);
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

    public int getClaimThresholdOnePlayer() {
        return claimThresholdOnePlayer;
    }

    public int getClaimThresholdSmallModifier() {
        return claimThresholdSmallModifier;
    }

    public int getClaimThresholdSmallOffset() {
        return claimThresholdSmallOffset;
    }

    public int getClaimThresholdLargeModifier() {
        return claimThresholdLargeModifier;
    }

    public int getExistenceThresholdOnePlayer() {
        return existenceThresholdOnePlayer;
    }

    public int getExistenceThresholdSmallModifier() {
        return existenceThresholdSmallModifier;
    }

    public int getExistenceThresholdSmallOffset() {
        return existenceThresholdSmallOffset;
    }

    public int getExistenceThresholdLargeModifierDynamic() {
        return existenceThresholdLargeModifierDynamic;
    }

    public int getExistenceThresholdLargeModifierFlat() {
        return existenceThresholdLargeModifierFlat;
    }

    public float getChunksPerMember() {
        return chunksPerMember;
    }

    public float getOutpostsPerMember() {
        return outpostsPerMember;
    }

    public Material getClaimBlockHome() {
        return claimBlockHome;
    }
}
