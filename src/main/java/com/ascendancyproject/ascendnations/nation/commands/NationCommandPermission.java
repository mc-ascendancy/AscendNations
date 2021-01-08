package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationPermission;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@NationCommandAnnotation(
        name = "permission",
        description = "Modify your nation's permissions.",
        minimumRole = NationRole.Chancellor
)
public class NationCommandPermission extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (args.length == 1) {
            list();
            return;
        }

        for (NationPermission permission : NationPermission.values()) {
            String permissionName = Language.format(player, "permission" + permission.name());

            if (!args[1].equalsIgnoreCase(permissionName))
                continue;

            if (nation.getPermissions().add(permission)) {
                Language.sendMessage(player, "permissionAdded", new String[]{"permissionName", permissionName});
            } else {
                nation.getPermissions().remove(permission);
                Language.sendMessage(player, "permissionRemoved", new String[]{"permissionName", permissionName});
            }

            return;
        }

        Language.sendMessage(player, "errorPermissionUnknown");
    }

    private void list() {

    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        ArrayList<String> results = new ArrayList<>();

        for (NationPermission permission : NationPermission.values())
            results.add(Language.format(player, "permission" + permission.name()));

        return results;
    }
}
