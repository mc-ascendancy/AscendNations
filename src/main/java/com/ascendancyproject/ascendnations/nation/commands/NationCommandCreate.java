package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.events.NationEventType;
import com.ascendancyproject.ascendnations.events.NationScriptEvent;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "create",
        description = "Form a new nation.",
        requiresNation = false,
        requiresNoNation = true
)
public class NationCommandCreate extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length < 2) {
            Language.sendMessage(player, "errorNationCreateNoNameProvided");
            return;
        }

        if (nation != null) {
            Language.sendMessage(player, "errorNationCreateAlreadyInNation");
            return;
        }

        String name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

        if (name.contains("-")) {
            Language.sendMessage(player, "errorNationCreateInvalidCharacter", new String[]{"badCharacter", "-"});
            return;
        }

        nation = new Nation(player, name);
        playerData.setNationUUID(nation.getUUID());

        Language.sendMessage(player, "nationCreated", new String[]{"nationName", name});
        new NationScriptEvent(NationEventType.creation, player.getName(), name);

        ClaimBlock.giveHome(player);
    }
}
