package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.rift.Rift;
import com.ascendancyproject.ascendnations.rift.RiftChunk;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Overclaim {
    public static final int tickFrequency = 20;

    public static HashMap<UUID, Overclaim> overclaims = new HashMap<>();

    private final long expires;
    private final Long chunk;

    public Overclaim(Player player, long duration, Long chunk, Nation attackingNation, Nation defendingNation) {
        expires = System.currentTimeMillis() + duration;
        this.chunk = chunk;

        Language.broadcastMessage("overclaimStartBroadcast", new String[][]{
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()}
        }, player.getUniqueId());

        Language.sendMessage(player, "overclaimStart",
                new String[]{"defendingNationName", defendingNation.getName()},
                new String[]{"duration", AscendNationsHelper.durationToString(duration)}
        );

        overclaims.put(player.getUniqueId(), this);
    }

    public void tick(UUID playerUUID) {
        if (System.currentTimeMillis() < expires)
            return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            overclaims.remove(playerUUID);
            return;
        }

        if (player.getChunk().getChunkKey() != chunk) {
            failBroadcast(player);
            Language.sendMessage(player, "overclaimFailMoved");
            overclaims.remove(playerUUID);
            return;
        }

        PlayerData playerData = PersistentData.instance.getPlayers().get(playerUUID);
        if (playerData.getNationUUID() == null) {
            overclaims.remove(playerUUID);
            return;
        }

        Nation attackingNation = PersistentData.instance.getNations().get(playerData.getNationUUID());

        UUID defendingNationUUID = ClaimChunks.chunks.get(chunk);
        if (defendingNationUUID == null) {
            overclaims.remove(playerUUID);
            return;
        }

        Nation defendingNation = PersistentData.instance.getNations().get(defendingNationUUID);
        if (defendingNation == null) {
            overclaims.remove(playerUUID);
            return;
        }

        if (defendingNation.isClaimChunk(chunk)) {
            if (chunk.equals(defendingNation.getHomeChunk())) {
                defendingNation.disband();

                Language.broadcastMessage("overclaimSuccessBroadcastHome", new String[][]{
                        new String[]{"attackingNationName", attackingNation.getName()},
                        new String[]{"defendingNationName", defendingNation.getName()}
                }, playerUUID);
                Language.sendMessage(player, "overclaimSuccess");

                ClaimChunks.claim(attackingNation, chunk);
                attackingNation.getPower().recalculate(attackingNation);

                overclaims.remove(playerUUID);
                return;
            }

            defendingNation.removeOutpost(chunk);
        }

        Rift rift = RiftConfig.getRift(player.getChunk().getChunkKey());
        if (rift != null)
            for (RiftChunk riftChunk : rift.getChunks())
                ClaimChunks.claim(attackingNation, defendingNation, riftChunk.getKey());
        else
            ClaimChunks.claim(attackingNation, defendingNation, chunk);

        HashSet<Long> touched = defendingNation.getTouched(chunk);
        int diff = defendingNation.getChunks().size() - touched.size() + (rift == null ? 1 : rift.getChunks().size());

        defendingNation.recalculateChunks(touched);

        defendingNation.getPower().recalculate(defendingNation);
        attackingNation.getPower().recalculate(attackingNation);

        Language.broadcastMessage("overclaimSuccessBroadcast", new String[][]{
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()},
                new String[]{"chunkCount", Integer.toString(diff)}
        }, playerUUID);
        Language.sendMessage(player, "overclaimSuccess");

        overclaims.remove(playerUUID);
    }

    public static boolean failOverclaim(Player player) {
        Overclaim overclaim = overclaims.get(player.getUniqueId());

        if (overclaim == null)
            return false;

        overclaim.failBroadcast(player);
        overclaims.remove(player.getUniqueId());
        return true;
    }

    private void failBroadcast(Player player) {
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());
        if (playerData.getNationUUID() == null) {
            return;
        }

        Nation attackingNation = PersistentData.instance.getNations().get(playerData.getNationUUID());

        UUID defendingNationUUID = ClaimChunks.chunks.get(chunk);
        if (defendingNationUUID == null) {
            return;
        }

        Nation defendingNation = PersistentData.instance.getNations().get(defendingNationUUID);
        if (defendingNation == null) {
            return;
        }

        Language.broadcastMessage("overclaimFailBroadcast", new String[][]{
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()}
        }, player.getUniqueId());
    }
}
