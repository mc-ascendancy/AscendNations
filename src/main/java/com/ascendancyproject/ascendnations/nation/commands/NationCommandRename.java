package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "rename",
        description = "Rename your nation.",
        minimumRole = NationRole.Chancellor
)
public class NationCommandRename extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length < 2) {
            Language.sendMessage(player, "errorNationRenameNoNameProvided");
            return;
        }

        String name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

        Language.sendMessage(player, "nationRename", new String[]{"nameNew", name}, new String[]{"nameOld", nation.getName()});
        nation.broadcast("nationRenameBroadcast", new String[][]{
                new String[]{"nameNew", name},
                new String[]{"nameOld", nation.getName()}
        }, player.getUniqueId());

        nation.setName(name);
    }
}
