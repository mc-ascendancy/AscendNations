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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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

        if (key.equals(nation.getHome())) {
            player.sendMessage(Language.getLine("errorChunkUnclaimHome"));
            return;
        }

        HashSet<Long> touched = getTouched(player.getChunk(), nation);
        int diff = nation.getChunks().size() - touched.size();

        if (!force && diff > 1) {
            player.sendMessage(Language.format("errorChunkUnclaimMultipleUnclaimed", new String[]{"chunkCount", Integer.toString(diff)}));
            return;
        }

        if (diff > 1) {
            for (Iterator<Long> it = nation.getChunks().iterator(); it.hasNext();) {
                Long nKey = it.next();

                if (!touched.contains(nKey)) {
                    ClaimChunks.chunks.remove(nKey);
                    it.remove();
                }
            }

            player.sendMessage(Language.format("chunkUnclaimForce", new String[]{"chunkCount", Integer.toString(diff)}));
        } else {
            ClaimChunks.chunks.remove(key);
            nation.getChunks().remove(key);

            player.sendMessage(Language.getLine("chunkUnclaim"));
        }
    }

    private HashSet<Long> getTouched(Chunk remove, Nation nation) {
        Long rKey = remove.getChunkKey();

        Vector home = new Vector(Block.getBlockKeyX(nation.getHome()) / 16, 0, Block.getBlockKeyZ(nation.getHome()) / 16);

        HashSet<Long> touched = new HashSet<>();
        touched.add(Chunk.getChunkKey(home.getBlockX(), home.getBlockZ()));

        Queue<Vector> q = new LinkedList<>();
        q.add(home);

        while (!q.isEmpty()) {
            for (Vector vector : getNeighbours(q.poll())) {
                Long key = Chunk.getChunkKey(vector.getBlockX(), vector.getBlockZ());

                if (!key.equals(rKey) && !touched.contains(key) && ClaimChunks.checkChunk(nation.getUUID(), key)) {
                    q.add(vector);
                    touched.add(key);
                }
            }
        }

        return touched;
    }

    private Vector[] getNeighbours(Vector vector) {
        int x = vector.getBlockX();
        int z = vector.getBlockZ();

        return new Vector[]{
                new Vector(x - 1, 0, z),
                new Vector(x + 1, 0, z),
                new Vector(x, 0, z - 1),
                new Vector(x, 0, z + 1)
        };
    }
}