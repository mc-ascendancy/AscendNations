package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.claim.Overclaim;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NationCommandAnnotation(
        name = "claim",
        description = "Claim territory for your nation.",
        aliases = {"claim"},
        minimumRole = NationRole.Commander
)
public class NationCommandClaim extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length == 1) {
            claim(player, nation);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "auto":
                claimAuto();
                break;

            case "home":
                if (nation.getHome() != null) {
                    player.sendMessage(Language.getLine("errorClaimHomeClaimed"));
                    return;
                }

                ClaimBlock.giveHome(player);
                break;

            case "outpost":
                if (nation.getHome() == null) {
                    player.sendMessage(Language.getLine("errorClaimOutpostNoHome"));
                    return;
                }

                if (!nation.hasOutpostClaims()) {
                    player.sendMessage(Language.format("errorClaimOutpostMaxClaimed", new String[]{"outpostCount", Integer.toString(nation.getPower().getOutpostsClaimable())}));
                    return;
                }

                ClaimBlock.giveOutpost(player);
                break;

            default:
                player.sendMessage(Language.format("errorNationClaimBadArgs", new String[]{"args", args[1]}));
        }
    }

    private void claim(Player player, Nation nation) {
        if (!nation.hasClaims()) {
            player.sendMessage(Language.format("errorChunkClaimNoClaims", new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())}));
            return;
        }

        if (!ClaimChunks.hasNeighbour(nation.getUUID(), player.getLocation())) {
            player.sendMessage(Language.getLine("errorChunkClaimNoNeighbour"));
            return;
        }

        Long key = player.getChunk().getChunkKey();

        UUID defendingNationUUID = ClaimChunks.chunks.get(key);
        if (defendingNationUUID != null) {
            if (defendingNationUUID.equals(nation.getUUID())) {
                player.sendMessage(Language.getLine("errorChunkClaimAlreadyOwnedByYou"));
                return;
            }

            Nation defendingNation = PersistentData.instance.getNations().get(defendingNationUUID);

            if (defendingNation.isClaimChunk(key)) {
                if (!defendingNation.isDestroyable()) {
                    player.sendMessage(Language.format("errorChunkClaimAlreadyOwnedClaim", new String[]{"defendingNationName", defendingNation.getName()}));
                    return;
                }

                new Overclaim(player, key.equals(defendingNation.getHomeChunk()) ? NationVariables.instance.getOverclaimDurationHome() : NationVariables.instance.getOverclaimDurationOutpost(), key, nation, defendingNation);
                return;
            }

            if (!defendingNation.isOverclaimable()) {
                player.sendMessage(Language.format("errorChunkClaimAlreadyOwned", new String[]{"defendingNationName", defendingNation.getName()}));
                return;
            }

            new Overclaim(player, NationVariables.instance.getOverclaimDuration(), key, nation, defendingNation);
            return;
        }

        ClaimChunks.claim(nation, player.getLocation());
        nation.getPower().recalculate(nation);

        player.sendMessage(Language.format("chunkClaim",
                new String[]{"chunksClaimed", Integer.toString(nation.getChunks().size())},
                new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())}));
    }

    private void claimAuto() {
        // TODO: claim auto.
    }
}
