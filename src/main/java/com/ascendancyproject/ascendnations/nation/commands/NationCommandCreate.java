package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NationCommandAnnotation(
        name = "create",
        description = "Form a new nation.",
        aliases = {"create", "form"},
        requiresNation = false
)
public class NationCommandCreate extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Language.getLine("errorNationCreateNoNameProvided"));
            return;
        }

        if (nation != null) {
            player.sendMessage(Language.getLine("errorNationCreateAlreadyInNation"));
            return;
        }

        nation = new Nation(player, String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length)));
        playerData.setNationUUID(nation.getUUID());

        player.sendMessage(Language.format("nationCreated", new String[]{"nationName", nation.getName()}));

        ClaimBlock.giveHome(player);
    }
}
