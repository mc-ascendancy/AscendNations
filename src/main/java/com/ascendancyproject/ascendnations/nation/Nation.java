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

        members.put(creator.getUniqueId(), new NationMember());

        PersistentData.instance.getNations().put(uuid, this);
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
