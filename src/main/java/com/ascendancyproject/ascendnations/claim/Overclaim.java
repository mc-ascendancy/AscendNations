package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class Overclaim {
    public static final int tickFrequency = 20;

    public static HashMap<UUID, Overclaim> overclaims = new HashMap<>();

    private final long expires;
    private final Long chunk;

    public Overclaim(Player player, long duration, Long chunk, Nation attackingNation, Nation defendingNation) {
        expires = System.currentTimeMillis() + duration;
        this.chunk = chunk;

        Bukkit.getServer().broadcastMessage(Language.format("overclaimStartBroadcast",
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()}
        ));

        player.sendMessage(Language.format("overclaimStart",
                new String[]{"defendingNationName", defendingNation.getName()},
                new String[]{"duration", AscendNationsHelper.durationToString(duration)}
        ));

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
            player.sendMessage(Language.getLine("overclaimFailMoved"));
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

                Bukkit.getServer().broadcastMessage(Language.format("overclaimSuccessBroadcastHome",
                        new String[]{"attackingNationName", attackingNation.getName()},
                        new String[]{"defendingNationName", defendingNation.getName()}
                ));
                player.sendMessage(Language.getLine("overclaimSuccess"));

                ClaimChunks.chunks.put(chunk, attackingNation.getUUID());
                attackingNation.getChunks().add(chunk);

                overclaims.remove(playerUUID);
                return;
            }

            World world = Bukkit.getWorld("world");

            for (Iterator<Long> it = defendingNation.getOutposts().iterator(); it.hasNext();) {
                Long outpost = it.next();
                Long outpostChunk = defendingNation.getOutpostChunk(outpost);

                if (outpostChunk.equals(chunk)) {
                    it.remove();
                    ClaimBlock.removeBlock(world.getBlockAtKey(outpost));

                    break;
                }
            }
        }

        ClaimChunks.chunks.put(chunk, attackingNation.getUUID());
        defendingNation.getChunks().remove(chunk);
        attackingNation.getChunks().add(chunk);

        HashSet<Long> touched = defendingNation.getTouched(chunk);
        int diff = defendingNation.getChunks().size() - touched.size() + 1;

        defendingNation.recalculateChunks(touched);

        Bukkit.getServer().broadcastMessage(Language.format("overclaimSuccessBroadcast",
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()},
                new String[]{"chunkCount", Integer.toString(diff)}
        ));
        player.sendMessage(Language.getLine("overclaimSuccess"));

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

        Bukkit.getServer().broadcastMessage(Language.format("overclaimFailBroadcast",
                new String[]{"attackingNationName", attackingNation.getName()},
                new String[]{"defendingNationName", defendingNation.getName()}
        ));
    }
}
