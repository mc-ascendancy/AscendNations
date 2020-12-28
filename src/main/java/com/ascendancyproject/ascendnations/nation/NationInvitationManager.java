package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NationInvitationManager {
    private static final int cleanupFrequency = 5 * 60 * 20;

    public static HashMap<UUID, NationInvitation> invitations;

    public static void init(AscendNations plugin) {
        invitations = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, NationInvitation> entry : invitations.entrySet())
                    if (entry.getValue().hasExpired())
                        invitations.remove(entry.getKey());
            }
        }.runTaskTimer(plugin, cleanupFrequency, cleanupFrequency);
    }

    public static UUID createInvitation(NationInvitation invitation) {
        UUID uuid = UUID.randomUUID();

        invitations.put(uuid, invitation);

        return uuid;
    }
}
