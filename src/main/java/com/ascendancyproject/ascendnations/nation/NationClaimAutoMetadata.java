package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import org.bukkit.metadata.MetadataValueAdapter;

public class NationClaimAutoMetadata extends MetadataValueAdapter {
    public static final String key = "an-claim-auto";

    public NationClaimAutoMetadata() {
        super(AscendNations.getInstance());
    }

    @Override
    public Object value() {
        return null;
    }

    @Override
    public void invalidate() {}
}
