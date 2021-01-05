package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationOutpost;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "outposts",
        description = "View the status of your nation's outposts."
)
public class NationCommandOutposts extends NationCommand {
    private static final int listingsPerPage = 3;

    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        int page;
        if (args.length >= 2)
            page = Integer.parseUnsignedInt(args[1]);
        else
            page = 1;

        StringBuilder listings = new StringBuilder();

        for (int i = (page - 1) * listingsPerPage; i < nation.getOutposts().size() && i < page * listingsPerPage; i++) {
            NationOutpost outpost = nation.getOutposts().get(nation.getOutpostsSequential().get(i));

            listings.append(Language.format(player, "nationOutpostsOutpost",
                    new String[]{"outpostNumber", Integer.toString(outpost.getNumber())},
                    new String[]{"outpostResupplyStatus", Language.format(player, "nationOutpostStatus" + outpost.getResupplyState().name())},
                    new String[]{"outpostResupplyDuration", AscendNationsHelper.durationToString(outpost.getResupplyExpiry() - System.currentTimeMillis())}
            ));
        }

        Language.sendMessage(player, "nationOutposts",
                new String[]{"page", Integer.toString(page)},
                new String[]{"pageMax", Integer.toString((nation.getOutposts().size() - 1) / listingsPerPage + 1)},
                new String[]{"outpostCount", Integer.toString(nation.getOutposts().size())},
                new String[]{"outpostMax", Integer.toString(nation.getPower().getOutpostsClaimable())},
                new String[]{"outpostListings", listings.toString()}
        );
    }
}
