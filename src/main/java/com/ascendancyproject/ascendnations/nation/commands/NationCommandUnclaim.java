package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@NationCommandAnnotation(
        name = "unclaim",
        description = "Unclaim your nation's territory to free up claims.",
        minimumRole = NationRole.Commander
)
public class NationCommandUnclaim extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        boolean force = false;

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase(Language.format(player, "chunkUnclaimForceSubcommand"))) {
                force = true;
            } else {
                player.sendMessage("errorChunkUnclaimBadArgs");
                return;
            }
        }

        Chunk remove = player.getLocation().getChunk();
        Long key = remove.getChunkKey();

        if (!nation.getChunks().contains(key)) {
            Language.sendMessage(player, "errorChunkUnclaimNotClaimed");
            return;
        }

        if (key.equals(nation.getHomeChunk())) {
            Language.sendMessage(player, "errorChunkUnclaimHome");
            return;
        }

        HashSet<Long> touched = nation.getTouched(player.getChunk().getChunkKey());
        int diff = nation.getChunks().size() - touched.size();

        if (!force && diff > 1) {
            Language.sendMessage(player, "errorChunkUnclaimMultipleUnclaimed", new String[]{"chunkCount", Integer.toString(diff)});
            return;
        }

        if (diff > 1) {
            nation.recalculateChunks(touched);
            Language.sendMessage(player, "chunkUnclaimForce", new String[]{"chunkCount", Integer.toString(diff)});
        } else {
            ClaimChunks.chunks.remove(key);
            nation.getChunks().remove(key);

            Language.sendMessage(player, "chunkUnclaim");
        }

        nation.removeOutpost(key);
        nation.getPower().recalculate(nation);
    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        return new ArrayList<>(Arrays.asList(Language.format(player, "chunkUnclaimForceSubcommand")));
    }
}
