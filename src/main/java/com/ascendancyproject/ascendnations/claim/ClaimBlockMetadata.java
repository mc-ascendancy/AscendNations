package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import org.bukkit.metadata.MetadataValueAdapter;

public class ClaimBlockMetadata extends MetadataValueAdapter {
    public static final String key = "an-claim";
    private ClaimBlockType type;

    protected ClaimBlockMetadata(ClaimBlockType type) {
        super(AscendNations.getInstance());
        this.type = type;
    }

    @Override
    public ClaimBlockType value() {
        return type;
    }

    @Override
    public void invalidate() {
        type = null;
    }
}
