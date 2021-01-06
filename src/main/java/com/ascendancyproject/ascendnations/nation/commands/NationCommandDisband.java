package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.events.NationEventType;
import com.ascendancyproject.ascendnations.events.NationScriptEvent;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "disband",
        description = "Disband your nation.",
        minimumRole = NationRole.Chancellor
)
public class NationCommandDisband extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        Language.sendMessage(player, "nationDisbanded", new String[]{"nationName", nation.getName()});
        nation.broadcast("nationDisbandedBroadcast", new String[][]{new String[]{"nationName", nation.getName()}}, player.getUniqueId());
        new NationScriptEvent(NationEventType.disbanded, nation.getName());

        nation.disband();
    }
}
