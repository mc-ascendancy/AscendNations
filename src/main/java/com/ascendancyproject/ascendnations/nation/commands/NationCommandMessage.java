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

import java.util.ArrayList;

@NationCommandAnnotation(
        name = "message",
        description = "Set your nation's messages.",
        minimumRole = NationRole.Chancellor
)
public class NationCommandMessage extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(Language.getLine("errorNationMessageNoField"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Language.getLine("errorNationMessageNoValue"));
            return;
        }

        String field = args[1].toLowerCase();
        if (!nation.getMessages().containsKey(field)) {
            player.sendMessage(Language.getLine("errorNationMessageUnknownField"));
            return;
        }

        String value = String.join(" ", (String[]) ArrayUtils.subarray(args, 2, args.length));
        nation.getMessages().put(field, value);

        player.sendMessage(Language.format("nationMessage", new String[]{"field", field}, new String[]{"value", value}));
    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        return new ArrayList<>(nation.getMessages().keySet());
    }
}
