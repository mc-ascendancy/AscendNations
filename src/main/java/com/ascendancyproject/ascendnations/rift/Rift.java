package com.ascendancyproject.ascendnations.rift;

import com.ascendancyproject.ascendnations.nation.Nation;

import java.util.ArrayList;

public class Rift {
    private int power;
    private ArrayList<RiftChunk> chunks;
    private Long checkChunk;

    public int getPower() {
        return power;
    }

    public ArrayList<RiftChunk> getChunks() {
        return chunks;
    }

    public boolean isCheckChunk(Long chunk) {
        return checkChunk.equals(chunk);
    }

    public void setCheckChunkIfEmpty(Long checkChunk) {
        if (this.checkChunk != null)
            return;

        this.checkChunk = checkChunk;
    }

    public boolean isOwned(Nation nation) {
        for (RiftChunk chunk : chunks)
            if (!nation.getChunks().contains(chunk.getKey()))
                return false;

        return true;
    }
}
