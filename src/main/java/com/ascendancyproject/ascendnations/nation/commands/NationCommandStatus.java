package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationPower;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "status",
        description = "Check your nation's power status.",
        aliases = {"status"}
)
public class NationCommandStatus extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        NationPower np = nation.getPower();

        player.sendMessage(Language.format("nationStatus",
                new String[]{"nationPower", Integer.toString(np.getTotal())},
                new String[]{"nationClaimThreshold", Integer.toString(np.getClaimThreshold())},
                new String[]{"nationExistenceThreshold", Integer.toString(np.getExistenceThreshold())}
        ));
    }
}
