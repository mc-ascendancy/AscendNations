package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

public class NationVariables {
    public static final String location = "nationvariables.json";

    public static NationVariables instance;

    private int maxNationPop;
    private int maxMemberPassivePower;
    private int maxMemberBonusPower;
    private int riftModifier;
    private int minNationPopClaimRift;
    private int memberPassivePowerStart;
    private int memberPassiveGainFrequency;
    private int memberPassiveDecayFrequency;
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
    private String claimBlockOutpostType;
    private Material claimBlockOutpost;

    private long lockoutDuration;
    private float lockoutFactor;
    private long lockoutChunkDuration;
    private int lockoutReminderFrequency;
    private long claimPunishmentDuration;

    private long overclaimDuration;
    private long overclaimDurationOutpost;
    private long overclaimDurationHome;

    private long resupplySatisfiedDuration;
    private long resupplyTimeout;
    private float resupplyDistance;
    private double resupplyHealth;
    private int resupplyReminderFrequency;

    private HashSet<String> protectedMobs;
    private HashSet<String> exemptMobDamageCause;
    private HashSet<String> protectedBlocks;

    private HashSet<String> goodSpawnBlocks;

    private int saveFrequency;

    private int mapBeaconLevitation;
    private String mapBeaconType;
    private Material mapBeaconMaterial;
    private String mapBeaconGlass;
    private Material mapBeaconGlassMaterial;
    private String mapBeaconGlassHome;
    private Material mapBeaconGlassHomeMaterial;
    private String mapBeaconGlassOutpost;
    private Material mapBeaconGlassOutpostMaterial;
    private long mapDuration;

    private int homePowerCost;

    private int titleFadeIn;
    private int titleStay;
    private int titleFadeOut;

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
        instance.claimBlockOutpost = Material.getMaterial(instance.claimBlockOutpostType);

        instance.mapBeaconMaterial = Material.getMaterial(instance.mapBeaconType);
        instance.mapBeaconGlassMaterial = Material.getMaterial(instance.mapBeaconGlass);
        instance.mapBeaconGlassHomeMaterial = Material.getMaterial(instance.mapBeaconGlassHome);
        instance.mapBeaconGlassOutpostMaterial = Material.getMaterial(instance.mapBeaconGlassOutpost);
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

    public int getMemberPassiveDecayFrequency() {
        return memberPassiveDecayFrequency;
    }

    public int getMemberPassivePowerStart() {
        return memberPassivePowerStart;
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

    public Material getClaimBlockOutpost() {
        return claimBlockOutpost;
    }

    public long getLockoutDuration() {
        return lockoutDuration;
    }

    public float getLockoutFactor() {
        return lockoutFactor;
    }

    public long getLockoutChunkDuration() {
        return lockoutChunkDuration;
    }

    public int getLockoutReminderFrequency() {
        return lockoutReminderFrequency;
    }

    public long getClaimPunishmentDuration() {
        return claimPunishmentDuration;
    }

    public long getOverclaimDuration() {
        return overclaimDuration;
    }

    public long getOverclaimDurationOutpost() {
        return overclaimDurationOutpost;
    }

    public long getOverclaimDurationHome() {
        return overclaimDurationHome;
    }

    public long getResupplySatisfiedDuration() {
        return resupplySatisfiedDuration;
    }

    public long getResupplyTimeout() {
        return resupplyTimeout;
    }

    public float getResupplyDistance() {
        return resupplyDistance;
    }

    public double getResupplyHealth() {
        return resupplyHealth;
    }

    public int getResupplyReminderFrequency() {
        return resupplyReminderFrequency;
    }

    public HashSet<String> getProtectedMobs() {
        return protectedMobs;
    }

    public HashSet<String> getExemptMobDamageCause() {
        return exemptMobDamageCause;
    }

    public HashSet<String> getProtectedBlocks() {
        return protectedBlocks;
    }

    public HashSet<String> getGoodSpawnBlocks() {
        return goodSpawnBlocks;
    }

    public int getSaveFrequency() {
        return saveFrequency;
    }

    public int getMapBeaconLevitation() {
        return mapBeaconLevitation;
    }

    public Material getMapBeaconMaterial() {
        return mapBeaconMaterial;
    }

    public Material getMapBeaconGlassMaterial() {
        return mapBeaconGlassMaterial;
    }

    public Material getMapBeaconGlassHomeMaterial() {
        return mapBeaconGlassHomeMaterial;
    }

    public Material getMapBeaconGlassOutpostMaterial() {
        return mapBeaconGlassOutpostMaterial;
    }

    public long getMapDuration() {
        return mapDuration;
    }

    public int getHomePowerCost() {
        return homePowerCost;
    }

    public int getTitleFadeIn() {
        return titleFadeIn;
    }

    public int getTitleStay() {
        return titleStay;
    }

    public int getTitleFadeOut() {
        return titleFadeOut;
    }
}
