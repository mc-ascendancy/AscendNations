package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.PersistentData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Nation {
    private final UUID uuid;
    private String name;
    private final HashMap<UUID, NationMember> members;

    public Nation(Player creator, String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        members = new HashMap<>();

        members.put(creator.getUniqueId(), new NationMember(NationRole.Chancellor));

        PersistentData.instance.getNations().put(uuid, this);
    }

    public void disband() {
        for (UUID memberUUID : members.keySet())
            PersistentData.instance.getPlayers().get(memberUUID).setNationUUID(null);

        PersistentData.instance.getNations().remove(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public HashMap<UUID, NationMember> getMembers() {
        return members;
    }
}
