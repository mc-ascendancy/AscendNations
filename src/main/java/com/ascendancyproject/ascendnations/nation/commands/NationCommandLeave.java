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
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "leave",
        description = "Leave your current nation."
)
public class NationCommandLeave extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (member.getRole() == NationRole.Chancellor) {
            Language.sendMessage(player, "errorNationLeaveIsChancellor");
            return;
        }

        playerData.setNationUUID(null);
        nation.getMembers().remove(player.getUniqueId());
        nation.getPower().recalculate(nation);

        Language.sendMessage(player, "nationLeave", new String[]{"nationName", nation.getName()});
        nation.broadcast("nationLeaveBroadcast", new String[][]{new String[]{"nationName", nation.getName()}, new String[]{"playerName", player.getName()}}, player.getUniqueId());
    }
}
