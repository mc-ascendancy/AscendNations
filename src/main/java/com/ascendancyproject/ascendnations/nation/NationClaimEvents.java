package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.events.NationEventType;
import com.ascendancyproject.ascendnations.events.NationScriptEvent;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.rift.Rift;
import com.ascendancyproject.ascendnations.rift.RiftChunk;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class NationClaimEvents implements Listener {
    public NationClaimEvents(AscendNations plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().equals(event.getTo().getChunk()))
            return;

        if (!event.getPlayer().hasMetadata(NationClaimAutoMetadata.key))
            return;

        PlayerData playerData = PersistentData.instance.getPlayers().get(event.getPlayer().getUniqueId());

        if (playerData.getNationUUID() == null) {
            cancelAuto(event.getPlayer());
            return;
        }

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationMember member = nation.getMembers().get(event.getPlayer().getUniqueId());

        if (member.getRole().ordinal() < NationRole.Commander.ordinal()) {
            cancelAuto(event.getPlayer());
            return;
        }

        Long key = event.getTo().getChunk().getChunkKey();
        UUID chunkOwner = ClaimChunks.chunks.get(key);

        if (chunkOwner != null) {
            if (chunkOwner.equals(nation.getUUID()))
                return;

            Nation ownerNation = PersistentData.instance.getNations().get(chunkOwner);

            cancelAuto(event.getPlayer());
            Language.sendMessage(event.getPlayer(), "errorChunkClaimAlreadyOwned", new String[]{"defendingNationName", ownerNation.getName()});
            return;
        }

        if (!ClaimChunks.hasNeighbour(nation.getUUID(), event.getTo())) {
            cancelAuto(event.getPlayer());
            return;
        }

        Rift rift = RiftConfig.getRift(key);
        int requiredClaims = rift == null ? 1 : rift.getChunks().size();

        if (!nation.hasClaims(requiredClaims)) {
            cancelAuto(event.getPlayer());

            if (rift == null)
                Language.sendMessage(event.getPlayer(), "errorChunkClaimNoClaims",
                        new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                        new String[]{"chunksClaimedMax", Integer.toString(nation.getPower().getChunksClaimable())}
                );
            else
                Language.sendMessage(event.getPlayer(), "errorChunkClaimNoClaimsRift",
                        new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                        new String[]{"chunksClaimedMax", Integer.toString(nation.getPower().getChunksClaimable())},
                        new String[]{"riftChunks", Integer.toString(requiredClaims)}
                );

            return;
        }

        if (rift != null) {
            for (RiftChunk riftChunk : rift.getChunks())
                ClaimChunks.claim(nation, riftChunk.getKey());

            Language.sendMessage(event.getPlayer(), "chunkClaimRift",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())},
                    new String[]{"riftChunks", Integer.toString(rift.getChunks().size())},
                    new String[]{"riftPower", Integer.toString(rift.getPower())}
            );
            new NationScriptEvent(NationEventType.rift_claim, nation.getName());
        } else {
            ClaimChunks.claim(nation, key);

            Language.sendMessage(event.getPlayer(), "chunkClaim",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())}
            );
        }

        nation.getPower().recalculate(nation);
    }

    public static void cancelAuto(Player player) {
        player.removeMetadata(NationClaimAutoMetadata.key, AscendNations.getInstance());
        Language.sendMessage(player, "chunkClaimAutoDisabled");
    }
}
