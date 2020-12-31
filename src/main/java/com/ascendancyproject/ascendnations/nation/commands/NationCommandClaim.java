package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NationCommandClaim extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length == 1) {
            claim(player, nation);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "auto":
                claimAuto();
                break;

            case "home":
                ClaimBlock.giveHome(player);
                break;

            case "outpost":
                // TODO: give outpost.
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

        if (ClaimChunks.claim(nation, player.getLocation())) {
            player.sendMessage(Language.format("chunkClaim",
                    new String[]{"chunksClaimed",Integer.toString(nation.getChunks().size())},
                    new String[]{"chunksClaimable", Integer.toString(nation.getPower().getChunksClaimable())}));
        } else {
            // Chunk already claimed.
            player.sendMessage(Language.getLine("errorChunkClaimAlreadyOwned"));
        }
    }

    private void claimAuto() {
        // TODO: claim auto.
    }

    public String getName() {
        return "claim";
    }

    public String getDescription() {
        return "Claim territory for your nation.";
    }

    public String[] getAliases() {
        return new String[]{"claim"};
    }

    @Override
    public NationRole minimumRole() {
        return NationRole.Commander;
    }
}
