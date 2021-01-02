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

import java.util.*;

public class Nation {
    private final UUID uuid;
    private String name;
    private final HashMap<UUID, NationMember> members;
    private final NationPower power;

    private Long home;
    private final HashMap<Long, NationOutpost> outposts;
    private final ArrayList<Long> outpostsSequential;
    private final HashSet<Long> chunks;

    private long claimPunishmentExpiry;

    public Nation(Player creator, String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        members = new HashMap<>();
        members.put(creator.getUniqueId(), new NationMember(NationRole.Chancellor));
        outposts = new HashMap<>();
        outpostsSequential = new ArrayList<>();
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

        for (Long outpost : outposts.keySet())
            ClaimBlock.removeBlock(world.getBlockAtKey(outpost));

        PersistentData.instance.getNations().remove(uuid);
    }

    public void broadcast(String message, UUID... exceptions) {
        HashSet<UUID> exceptionSet = new HashSet<>(Arrays.asList(exceptions));

        for (UUID member : members.keySet()) {
            if (exceptionSet.contains(member))
                continue;

            Player player = Bukkit.getPlayer(member);

            if (player != null)
                player.sendMessage(message);
        }
    }

    public boolean lacksPermissions(UUID playerUUID, NationRole minimumRole) {
        NationMember nationMember = members.get(playerUUID);

        return nationMember.getRole().ordinal() < minimumRole.ordinal();
    }

    public boolean hasMemberSlots() {
        return members.size() < NationVariables.instance.getMaxNationPop();
    }

    public boolean hasClaims() {
        return hasClaims(1);
    }

    public boolean hasClaims(int count) {
        return chunks.size() + count <= power.getChunksClaimable();
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

        for (Long outpost : outposts.keySet())
            if (key.equals(getOutpostChunk(outpost)))
                return true;

        return false;
    }

    public void recalculateChunks(HashSet<Long> touched) {
        for (Iterator<Long> it = chunks.iterator(); it.hasNext();) {
            Long nKey = it.next();

            if (!touched.contains(nKey)) {
                ClaimChunks.chunks.remove(nKey);
                it.remove();
            }
        }
    }

    public HashSet<Long> getTouched(Long rKey) {
        HashSet<Long> touched = new HashSet<>();
        Queue<Vector> q = new LinkedList<>();

        touched.add(getHomeChunk());
        q.add(getHomeVector());

        for (Long outpost : outposts.keySet()) {
            Long outpostChunk = getOutpostChunk(outpost);

            if (!outpostChunk.equals(rKey)) {
                touched.add(outpostChunk);
                q.add(getOutpostVector(outpost));
            }
        }

        while (!q.isEmpty()) {
            for (Vector vector : getNeighbours(q.poll())) {
                Long key = Chunk.getChunkKey(vector.getBlockX(), vector.getBlockZ());

                if (!key.equals(rKey) && !touched.contains(key) && chunks.contains(key)) {
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

    public ArrayList<Long> getOutpostsSequential() {
        return outpostsSequential;
    }

    public HashMap<Long, NationOutpost> getOutposts() {
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

    public boolean isClaimPunished() {
        return claimPunishmentExpiry > System.currentTimeMillis();
    }

    public long getClaimPunishmentExpiry() {
        return claimPunishmentExpiry;
    }

    public void setClaimPunishmentExpiry(long claimPunishmentExpiry) {
        this.claimPunishmentExpiry = claimPunishmentExpiry;
    }
}
