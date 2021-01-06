package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.*;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.claim.Overclaim;
import com.ascendancyproject.ascendnations.events.NationEventType;
import com.ascendancyproject.ascendnations.events.NationScriptEvent;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.*;
import com.ascendancyproject.ascendnations.rift.Rift;
import com.ascendancyproject.ascendnations.rift.RiftChunk;
import com.ascendancyproject.ascendnations.rift.RiftConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

@NationCommandAnnotation(
        name = "claim",
        description = "Claim territory for your nation.",
        minimumRole = NationRole.Commander
)
public class NationCommandClaim extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (nation.isClaimPunished()) {
            Language.sendMessage(player, "errorNationClaimReclaimPunishment",
                    new String[]{"duration", AscendNationsHelper.durationToString(nation.getClaimPunishmentExpiry() - System.currentTimeMillis())}
            );
            return;
        }

        if (args.length == 1) {
            claim(player, nation);
            return;
        }

        if (Language.format(player, "chunkClaimAutoSubcommand").equalsIgnoreCase(args[1])) {
            claimAuto(player, nation);
        } else if (Language.format(player, "blockHomeSubcommand").equalsIgnoreCase(args[1])) {
            if (nation.getHome() != null) {
                Language.sendMessage(player, "errorClaimHomeClaimed");
                return;
            }

            ClaimBlock.giveHome(player);
        } else if (Language.format(player, "blockOutpostSubcommand").equalsIgnoreCase(args[1])) {
            if (nation.getHome() == null) {
                Language.sendMessage(player, "errorClaimOutpostNoHome");
                return;
            }

            if (!nation.hasOutpostClaims()) {
                Language.sendMessage(player, "errorClaimOutpostMaxClaimed", new String[]{"outpostCount", Integer.toString(nation.getPower().getOutpostsClaimable())});
                return;
            }

            ClaimBlock.giveOutpost(player);
        } else {
            Language.sendMessage(player, "errorNationClaimBadArgs", new String[]{"args", args[1]});
        }
    }

    private void claim(Player player, Nation nation) {
        if (!nation.hasClaims()) {
            Language.sendMessage(player, "errorChunkClaimNoClaims",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimedMax", Integer.toString(nation.getPower().getChunksClaimable())}
            );
            return;
        }

        if (!ClaimChunks.hasNeighbour(nation.getUUID(), player.getLocation())) {
            Language.sendMessage(player, "errorChunkClaimNoNeighbour");
            return;
        }

        Long key = player.getChunk().getChunkKey();
        Rift rift = RiftConfig.getRift(key);

        if (rift != null && !nation.hasClaims(rift.getChunks().size())) {
            Language.sendMessage(player, "errorChunkClaimNoClaimsRift",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())},
                    new String[]{"riftChunks", Integer.toString(rift.getChunks().size())}
            );
            return;
        }

        UUID defendingNationUUID = ClaimChunks.chunks.get(key);
        if (defendingNationUUID != null) {
            if (defendingNationUUID.equals(nation.getUUID())) {
                Language.sendMessage(player, "errorChunkClaimAlreadyOwnedByYou");
                return;
            }

            Nation defendingNation = PersistentData.instance.getNations().get(defendingNationUUID);

            if (defendingNation.isClaimChunk(key)) {
                if (!defendingNation.isDestroyable()) {
                    Language.sendMessage(player, "errorChunkClaimAlreadyOwnedClaim", new String[]{"defendingNationName", defendingNation.getName()});
                    return;
                }

                new Overclaim(player, key.equals(defendingNation.getHomeChunk()) ? NationVariables.instance.getOverclaimDurationHome() : NationVariables.instance.getOverclaimDurationOutpost(), key, nation, defendingNation);
                return;
            }

            if (!defendingNation.isOverclaimable()) {
                Language.sendMessage(player, "errorChunkClaimAlreadyOwned", new String[]{"defendingNationName", defendingNation.getName()});
                return;
            }

            new Overclaim(player, NationVariables.instance.getOverclaimDuration(), key, nation, defendingNation);
            return;
        }

        if (rift != null) {
            for (RiftChunk riftChunk : rift.getChunks())
                ClaimChunks.claim(nation, riftChunk.getKey());

            Language.sendMessage(player, "chunkClaimRift",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())},
                    new String[]{"riftChunks", Integer.toString(rift.getChunks().size())},
                    new String[]{"riftPower", Integer.toString(rift.getPower())}
            );
            new NationScriptEvent(NationEventType.rift_claim, nation.getName());
        } else {
            ClaimChunks.claim(nation, player.getChunk().getChunkKey());

            Language.sendMessage(player, "chunkClaim",
                    new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())}
            );
        }

        nation.getPower().recalculate(nation);
    }

    private void claimAuto(Player player, Nation nation) {
        if (player.hasMetadata(NationClaimAutoMetadata.key)) {
            NationClaimEvents.cancelAuto(player);
            return;
        }

        if (!nation.getChunks().contains(player.getChunk().getChunkKey())) {
            Language.sendMessage(player, "errorChunkClaimAutoNotInClaim");
            return;
        }

        player.setMetadata(NationClaimAutoMetadata.key, new NationClaimAutoMetadata());
        Language.sendMessage(player, "chunkClaimAutoEnabled");
    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        ArrayList<String> suggestions = new ArrayList<>();

        suggestions.add(Language.format(player, "chunkClaimAutoSubcommand"));

        if (nation.getHome() == null)
            suggestions.add(Language.format(player, "blockHomeSubcommand"));

        if (nation.hasOutpostClaims())
            suggestions.add(Language.format(player, "blockOutpostSubcommand"));

        return suggestions;
    }
}
