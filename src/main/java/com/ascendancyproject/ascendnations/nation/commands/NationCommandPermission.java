package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationPermission;
import com.ascendancyproject.ascendnations.nation.NationRole;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
            list(player, playerData, nation);
            return;
        }

        for (NationPermission permission : NationPermission.values()) {
            String permissionName = Language.format(player, "permissionType" + permission.name());

            if (!args[1].equalsIgnoreCase(permissionName))
                continue;

            if (!nation.getPermissions().add(permission))
                nation.getPermissions().remove(permission);

            Language.sendMessage(player, "permissionToggled",
                    new String[]{"permission", Language.format(player, "permissionType" + permission.name())},
                    new String[]{"privacy", nation.permissionPrivacyString(player, permission)}
            );

            return;
        }

        Language.sendMessage(player, "errorPermissionUnknown");
    }

    private void list(Player player, PlayerData playerData, Nation nation) {
        // Create the builder with the message title.
        ComponentBuilder builder = new ComponentBuilder(Language.format(player, "permission",
                new String[]{"nationName", nation.getName()}
        ) + "\n ");

        for (NationPermission permission : NationPermission.values()) {
            TextComponent component = new TextComponent("\n" + Language.format(player, "permissionEntry",
                    new String[]{"permission", Language.format(player, "permissionType" + permission.name())},
                    new String[]{"privacy", nation.permissionPrivacyString(player, permission)}
            ) + " ");
            builder.append(component, ComponentBuilder.FormatRetention.NONE);

            TextComponent clickable = new TextComponent(Language.format(player, "permissionEntryClickable"));
            clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/nation " + Language.getLanguage(playerData).getCommands().get("permission") + " " + Language.format(player, "permissionType" + permission.name())
            ));
            clickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.format(player, "permissionEntryClickableTooltip"))));
            builder.append(clickable, ComponentBuilder.FormatRetention.NONE);
        }

        player.sendMessage(builder.create());
    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        ArrayList<String> results = new ArrayList<>();

        for (NationPermission permission : NationPermission.values())
            results.add(Language.format(player, "permissionType" + permission.name()).toLowerCase());

        return results;
    }
}
