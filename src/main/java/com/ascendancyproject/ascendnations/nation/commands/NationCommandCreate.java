package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.claim.ClaimBlock;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    public String getName() {
        return "create";
    }

    public String getDescription() {
        return "Form a new nation.";
    }

    public String[] getAliases() {
        return new String[]{"create", "form"};
    }

    @Override
    public boolean requiresNation() {
        return false;
    }
}
