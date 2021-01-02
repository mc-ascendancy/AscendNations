package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Minecart;

import java.util.HashSet;
import java.util.UUID;

public class NationOutpost {
    private int number;

    private long resupplyExpiry;
    private NationOutpostResupply resupplyState;
    private UUID minecartUUID;

    public NationOutpost(Nation nation, Long key) {
        number = 1;
        for (Long outpostKey : nation.getOutpostsSequential()) {
            if (nation.getOutposts().get(outpostKey).number != number)
                break;

            number++;
        }

        resupplied();
        nation.getOutposts().put(key, this);
        nation.getOutpostsSequential().add(key);
    }

    public void tick(Nation nation, Long key) {
        if (resupplyState.equals(NationOutpostResupply.InProgress)) {
            World world = Bukkit.getWorld("world");

            Minecart minecart = (Minecart) world.getEntity(minecartUUID);
            if (minecart == null) {
                spawnMinecart(nation, true);
                return;
            }

            if (minecart.getLocation().distance(world.getBlockAtKey(key).getLocation()) <= NationVariables.instance.getResupplyDistance()) {
                resupplied();
                minecart.remove();
                return;
            }
        }

        if (resupplyExpiry > System.currentTimeMillis())
            return;

        if (resupplyState.equals(NationOutpostResupply.Satisfied)) {
            resupplyState = NationOutpostResupply.InProgress;
            resupplyExpiry = System.currentTimeMillis() + NationVariables.instance.getResupplyTimeout();

            spawnMinecart(nation, false);

            nation.broadcast(Language.format("resupplySpawned",
                    new String[]{"outpostNumber", Integer.toString(number)},
                    new String[]{"duration", AscendNationsHelper.durationToString(NationVariables.instance.getResupplyTimeout())}
            ));

            return;
        }

        ClaimBlock.removeBlock(Bukkit.getWorld("world").getBlockAtKey(key));
        ClaimChunks.unclaim(nation, nation.getOutpostChunk(key));

        nation.getOutposts().remove(key);
        nation.getOutpostsSequential().remove(key);

        HashSet<Long> touched = nation.getTouched(key);
        int diff = nation.getChunks().size() - touched.size() + 1;

        nation.recalculateChunks(touched);

        nation.broadcast(Language.format("resupplyFail",
                new String[]{"outpostNumber", Integer.toString(number)},
                new String[]{"chunkCount", Integer.toString(diff)}
        ));
    }

    public void reminderTick(Nation nation) {
        if (resupplyState.equals(NationOutpostResupply.InProgress))
            nation.broadcast(Language.format("resupplyReminder",
                    new String[]{"outpostNumber", Integer.toString(number)},
                    new String[]{"duration", AscendNationsHelper.durationToString(resupplyExpiry - System.currentTimeMillis())}
            ));
    }

    private void resupplied() {
        resupplyExpiry = System.currentTimeMillis() + NationVariables.instance.getResupplySatisfiedDuration();
        resupplyState = NationOutpostResupply.Satisfied;
    }

    public void spawnMinecart(Nation nation, boolean respawn) {
        World world = Bukkit.getWorld("world");

        Minecart minecart = world.spawn(world.getBlockAtKey(nation.getHome()).getLocation().add(0, 1, 0), Minecart.class);
        minecartUUID = minecart.getUniqueId();
    }

    public int getNumber() {
        return number;
    }
}
