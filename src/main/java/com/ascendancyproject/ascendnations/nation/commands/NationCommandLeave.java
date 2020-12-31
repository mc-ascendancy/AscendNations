package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NationCommandAnnotation(
        name = "leave",
        description = "Leave your current nation.",
        aliases = {"leave"}
)
public class NationCommandLeave extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (member.getRole() == NationRole.Chancellor) {
            player.sendMessage(Language.getLine("errorNationLeaveIsChancellor"));
            return;
        }

        playerData.setNationUUID(null);
        nation.getMembers().remove(player.getUniqueId());
        nation.getPower().recalculate(nation);

        player.sendMessage(Language.format("nationLeave", new String[]{"nationName", nation.getName()}));
    }
}
