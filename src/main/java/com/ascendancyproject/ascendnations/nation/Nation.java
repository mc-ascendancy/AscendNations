package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.PersistentData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Nation {
    private final UUID uuid;
    private String name;
    private final HashMap<UUID, NationMember> members;
    private final NationPower power;

    public Nation(Player creator, String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        members = new HashMap<>();

        members.put(creator.getUniqueId(), new NationMember(NationRole.Chancellor));

        power = new NationPower(this);

        PersistentData.instance.getNations().put(uuid, this);
    }

    public void disband() {
        for (UUID memberUUID : members.keySet())
            PersistentData.instance.getPlayers().get(memberUUID).setNationUUID(null);

        PersistentData.instance.getNations().remove(uuid);
    }

    public boolean lacksPermissions(UUID playerUUID, NationRole minimumRole) {
        NationMember nationMember = members.get(playerUUID);

        return nationMember.getRole().ordinal() < minimumRole.ordinal();
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

    public NationPower getPower() {
        return power;
    }
}
