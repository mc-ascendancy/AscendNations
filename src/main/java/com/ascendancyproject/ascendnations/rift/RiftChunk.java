package com.ascendancyproject.ascendnations.rift;

import org.bukkit.Chunk;

public class RiftChunk {
    private int x;
    private int z;

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Long getKey() {
        return Chunk.getChunkKey(x, z);
    }
}
