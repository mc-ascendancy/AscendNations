package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.claim.ClaimChunks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
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
    private final HashSet<Long> outposts;
    private final HashSet<Long> chunks;

    public Nation(Player creator, String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        members = new HashMap<>();
        members.put(creator.getUniqueId(), new NationMember(NationRole.Chancellor));
        outposts = new HashSet<>();
        chunks = new HashSet<>();
        power = new NationPower(this);

        PersistentData.instance.getNations().put(uuid, this);
    }

    public void disband() {
        for (UUID memberUUID : members.keySet())
            PersistentData.instance.getPlayers().get(memberUUID).setNationUUID(null);

        for (Long chunk : chunks)
            ClaimChunks.chunks.remove(chunk);

        World world = Bukkit.getWorld("world");

        if (home != null)
            ClaimBlock.removeBlock(world.getBlockAtKey(home));

        for (Long outpost : outposts)
            ClaimBlock.removeBlock(world.getBlockAtKey(outpost));

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

    public boolean hasOutpostClaims() {
        return outposts.size() < power.getOutpostsClaimable();
    }

    public boolean isOverclaimable() {
        return power.getTotal() < power.getClaimThreshold() && power.getLockoutExpiry() != 0L && power.getLockoutExpiry() < System.currentTimeMillis();
    }

    public boolean isDestroyable() {
        return power.getTotal() < power.getExistenceThreshold() && power.getLockoutExpiry() != 0L && power.getLockoutExpiry() < System.currentTimeMillis();
    }

    public boolean isClaimChunk(Long key) {
        if (key.equals(getHomeChunk()))
            return true;

        for (Long outpost : outposts)
            if (key.equals(getOutpostChunk(outpost)))
                return true;

        return false;
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

    public HashSet<Long> getOutposts() {
        return outposts;
    }

    public Long getOutpostChunk(Long outpost) {
        return Chunk.getChunkKey(Block.getBlockKeyX(outpost) / 16, Block.getBlockKeyZ(outpost) / 16);
    }

    public Vector getOutpostVector(Long outpost) {
        return new Vector(Block.getBlockKeyX(outpost) / 16, 0, Block.getBlockKeyZ(outpost) / 16);
    }

    public HashSet<Long> getChunks() {
        return chunks;
    }
}
