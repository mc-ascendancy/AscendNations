package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NationTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return null;

        Player player = (Player) sender;
        PlayerData playerData = PersistentData.instance.getPlayers().get(player.getUniqueId());

        Nation nation = PersistentData.instance.getNations().get(playerData.getNationUUID());
        NationMember member = nation == null ? null : nation.getMembers().get(player.getUniqueId());

        if (args.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();

            for (Map.Entry<String, NationCommand> entry : CommandNation.commandMap.entrySet()) {
                NationCommandAnnotation annotation = entry.getValue().getAnnotation();

                if (annotation.hidden())
                    continue;

                if (nation == null) {
                    if (annotation.requiresNation())
                        continue;
                } else {
                    if (annotation.requiresNoNation())
                        continue;

                    if (member.getRole().ordinal() < annotation.minimumRole().ordinal())
                        continue;
                }

                suggestions.add(entry.getKey());
            }

            return suggestions;
        }

        if (args.length == 2) {
            NationCommand nationCommand = CommandNation.commandMap.get(args[0].toLowerCase());
            if (nationCommand == null)
                return null;

            NationCommandAnnotation annotation = nationCommand.getAnnotation();

            if (nation == null) {
                if (annotation.requiresNation())
                    return null;
            } else {
                if (annotation.requiresNoNation())
                    return null;

                if (member.getRole().ordinal() < annotation.minimumRole().ordinal())
                    return null;
            }

            return nationCommand.getAutocomplete(player, nation, member);
        }

        return null;
    }
}
