package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;

@NationCommandAnnotation(
        name = "unclaim",
        description = "Unclaim your nation's territory to free up claims.",
        aliases = {"unclaim"},
        minimumRole = NationRole.Commander
)
public class NationCommandUnclaim extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        boolean force = false;

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("force")) {
                force = true;
            } else {
                player.sendMessage("errorChunkUnclaimBadArgs");
                return;
            }
        }

        Chunk remove = player.getLocation().getChunk();
        Long key = remove.getChunkKey();

        if (!nation.getChunks().contains(key)) {
            player.sendMessage(Language.getLine("errorChunkUnclaimNotClaimed"));
            return;
        }

        if (key.equals(nation.getHomeChunk())) {
            player.sendMessage(Language.getLine("errorChunkUnclaimHome"));
            return;
        }

        HashSet<Long> touched = nation.getTouched(player.getChunk().getChunkKey());
        int diff = nation.getChunks().size() - touched.size();

        if (!force && diff > 1) {
            player.sendMessage(Language.format("errorChunkUnclaimMultipleUnclaimed", new String[]{"chunkCount", Integer.toString(diff)}));
            return;
        }

        if (diff > 1) {
            nation.recalculateChunks(touched);
            player.sendMessage(Language.format("chunkUnclaimForce", new String[]{"chunkCount", Integer.toString(diff)}));
        } else {
            ClaimChunks.chunks.remove(key);
            nation.getChunks().remove(key);

            player.sendMessage(Language.getLine("chunkUnclaim"));
        }

        for (Iterator<Long> it = nation.getOutposts().keySet().iterator(); it.hasNext();) {
            Long outpost = it.next();
            Long outpostChunk = nation.getOutpostChunk(outpost);

            if (outpostChunk.equals(key)) {
                it.remove();
                nation.getOutpostsSequential().remove(outpost);
                ClaimBlock.removeBlock(remove.getWorld().getBlockAtKey(outpost));

                break;
            }
        }

        nation.getPower().recalculate(nation);
    }
}
