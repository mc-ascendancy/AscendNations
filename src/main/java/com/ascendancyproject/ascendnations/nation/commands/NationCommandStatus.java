package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationPower;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "status",
        description = "Check your nation's power status."
)
public class NationCommandStatus extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        NationPower np = nation.getPower();

        long protection;
        String statusColour;

        if (nation.getPower().getLockoutExpiry() == 0L) {
            protection = NationVariables.instance.getLockoutDuration();
            statusColour = Language.format(player, "nationStatusSafe");
        } else if (np.getLockoutExpiry() > System.currentTimeMillis()) {
            protection = nation.getPower().getLockoutExpiry() - System.currentTimeMillis();
            statusColour = Language.format(player, "nationStatusWarning");
        } else {
            protection = 0L;
            statusColour = Language.format(player, "nationStatusDanger");
        }

        String protectionString = protection != 0L ? AscendNationsHelper.durationToString(protection) : Language.format(player, "nationStatusProtectionExpired");

        String chunkProtectionString = AscendNationsHelper.durationToString(
                nation.getPower().getChunkLockoutExpiry() == 0 ?
                        NationVariables.instance.getLockoutChunkDuration() :
                        nation.getPower().getChunkLockoutExpiry() - System.currentTimeMillis()
        );
        String statusColourChunk = nation.getPower().getChunkLockoutExpiry() == 0 ?
                Language.format(player, "nationStatusSafe") :
                Language.format(player, "nationStatusWarning");

        Language.sendMessage(player, "nationStatus",
                new String[]{"nationPower", Integer.toString(np.getTotal())},
                new String[]{"nationClaimThreshold", Integer.toString(np.getClaimThreshold())},
                new String[]{"nationExistenceThreshold", Integer.toString(np.getExistenceThreshold())},
                new String[]{"nationChunkCount", Integer.toString(nation.getChunks().size())},
                new String[]{"nationChunkMax", Integer.toString(nation.getPower().getChunksClaimable())},
                new String[]{"nationOutpostCount", Integer.toString(nation.getOutposts().size())},
                new String[]{"nationOutpostMax", Integer.toString(nation.getPower().getOutpostsClaimable())},
                new String[]{"nationProtectionDuration", protectionString},
                new String[]{"nationChunkProtectionDuration", chunkProtectionString},
                new String[]{"status", statusColour},
                new String[]{"statusChunk", statusColourChunk}
        );
    }
}
