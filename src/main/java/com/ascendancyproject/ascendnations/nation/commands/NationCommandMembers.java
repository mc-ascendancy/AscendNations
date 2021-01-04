package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@NationCommandAnnotation(
        name = "members",
        description = "View all of your nation's members."
)
public class NationCommandMembers extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        String chancellor = null;
        ArrayList<String> commanders = new ArrayList<>();
        ArrayList<String> citizens = new ArrayList<>();

        for (Map.Entry<UUID, NationMember> entry : nation.getMembers().entrySet()) {
            switch (entry.getValue().getRole()) {
                case Citizen:
                    citizens.add(Bukkit.getOfflinePlayer(entry.getKey()).getName());
                    break;

                case Commander:
                    commanders.add(Bukkit.getOfflinePlayer(entry.getKey()).getName());
                    break;

                case Chancellor:
                    chancellor = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    break;
            }
        }

        player.sendMessage(Language.format("nationMembers",
                new String[]{"nationName", nation.getName()},
                new String[]{"chancellor", chancellor},
                new String[]{"commanders", String.join(", ", commanders)},
                new String[]{"citizens", String.join(", ", citizens)}
        ));
    }
}
