package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.PersistentData;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Nation {
    private final UUID uuid;
    private String name;
    private final HashMap<UUID, NationMember> members;
    private final NationPower power;

    private Long home;
    private final HashSet<Long> chunks;

    public Nation(Player creator, String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        members = new HashMap<>();
        members.put(creator.getUniqueId(), new NationMember(NationRole.Chancellor));
        power = new NationPower(this);
        chunks = new HashSet<>();

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

    public boolean hasMemberSlots() {
        return members.size() < NationVariables.instance.getMaxNationPop();
    }

    public boolean hasClaims() {
        return chunks.size() < power.getChunksClaimable();
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

    public Long getHome() {
        return home;
    }

    public Long getHomeChunk() {
        return Chunk.getChunkKey(Block.getBlockKeyX(home) / 16, Block.getBlockKeyZ(home) / 16);
    }

    public Vector getHomeVector() {
        return new Vector(Block.getBlockKeyX(home) / 16, 0, Block.getBlockKeyZ(home) / 16);
    }

    public void setHome(Long home) {
        this.home = home;
    }

    public HashSet<Long> getChunks() {
        return chunks;
    }
}
