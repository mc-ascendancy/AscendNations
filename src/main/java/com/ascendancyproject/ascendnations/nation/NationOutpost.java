package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import com.ascendancyproject.ascendnations.events.NationEventType;
import com.ascendancyproject.ascendnations.events.NationScriptEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.UUID;

public class NationOutpost {
    public static final NamespacedKey nbtKeyHealth = new NamespacedKey(AscendNations.getInstance(), "an-resupply-minecart-health");
    public static final NamespacedKey nbtKeyOutpost = new NamespacedKey(AscendNations.getInstance(), "an-resupply-minecart-outpost");

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
            if (minecart != null && minecart.getLocation().distance(world.getBlockAtKey(key).getLocation()) <= NationVariables.instance.getResupplyDistance()) {
                nation.broadcast("resupplySuccess", new String[]{"outpostNumber", Integer.toString(number)});
                new NationScriptEvent(NationEventType.resupply, "success", nation.getName());

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

            spawnMinecart(nation, key, false);

            nation.broadcast("resupplySpawned",
                    new String[]{"outpostNumber", Integer.toString(number)},
                    new String[]{"duration", AscendNationsHelper.durationToString(NationVariables.instance.getResupplyTimeout())}
            );

            return;
        }

        destroy(nation, key);
        nation.getOutposts().remove(key);

        HashSet<Long> touched = nation.getTouched(key);
        int diff = nation.getChunks().size() - touched.size() + 1;

        nation.recalculateChunks(touched);

        nation.broadcast("resupplyFail", new String[]{"outpostNumber", Integer.toString(number)},
                new String[]{"chunkCount", Integer.toString(diff)});
        new NationScriptEvent(NationEventType.resupply, "fail", nation.getName());
    }

    public void reminderTick(Nation nation) {
        if (resupplyState.equals(NationOutpostResupply.InProgress))
            nation.broadcast("resupplyReminder",
                    new String[]{"outpostNumber", Integer.toString(number)},
                    new String[]{"duration", AscendNationsHelper.durationToString(resupplyExpiry - System.currentTimeMillis())}
            );
    }

    private void resupplied() {
        resupplyExpiry = System.currentTimeMillis() + NationVariables.instance.getResupplySatisfiedDuration();
        resupplyState = NationOutpostResupply.Satisfied;
    }

    public void spawnMinecart(Nation nation, Long key, boolean respawn) {
        if (respawn)
            nation.broadcast("resupplyRespawn",
                    new String[]{"outpostNumber", Integer.toString(number)},
                    new String[]{"duration", AscendNationsHelper.durationToString(resupplyExpiry - System.currentTimeMillis())}
            );

        World world = Bukkit.getWorld("world");

        Minecart minecart = world.spawn(world.getBlockAtKey(nation.getHome()).getLocation().add(0, 1, 0), Minecart.class);

        PersistentDataContainer data = minecart.getPersistentDataContainer();
        data.set(nbtKeyHealth, PersistentDataType.DOUBLE, NationVariables.instance.getResupplyHealth());
        data.set(nbtKeyOutpost, PersistentDataType.LONG, key);

        minecartUUID = minecart.getUniqueId();
    }

    public void destroy(Nation nation, Long key) {
        nation.getOutpostsSequential().remove(key);

        if (minecartUUID != null) {
            Entity entity = Bukkit.getServer().getEntity(minecartUUID);
            if (entity != null)
                entity.remove();
        }

        ClaimChunks.unclaim(nation, Nation.getOutpostChunk(key));
        ClaimBlock.removeBlock(Bukkit.getWorld("world").getBlockAtKey(key));
    }

    public int getNumber() {
        return number;
    }

    public long getResupplyExpiry() {
        return resupplyExpiry;
    }

    public NationOutpostResupply getResupplyState() {
        return resupplyState;
    }

    public UUID getMinecartUUID() {
        return minecartUUID;
    }
}
