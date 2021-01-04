package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PersistentData;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@NationCommandAnnotation(
        name = "list",
        description = "View a list of all nations.",
        requiresNation = false
)
public class NationCommandList extends NationCommand {
    private static final int listingsPerPage = 6;

    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseUnsignedInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Language.getLine("errorNationListBadPage"));
                return;
            }
        }

        int nationCount = PersistentData.instance.getNations().size();
        int pageMax = (nationCount - 1) / listingsPerPage + 1;

        if (nationCount == 0) {
            player.sendMessage(Language.getLine("errorNationListNoNations"));
            return;
        }

        int pageStart = (page - 1) * listingsPerPage;
        int pageEnd = Math.min(pageStart + listingsPerPage, nationCount);

        if (page < 1 || pageStart >= nationCount) {
            player.sendMessage(Language.getLine("errorNationListBadPage"));
            return;
        }

        ArrayList<Map.Entry<UUID, Nation>> nations = new ArrayList<>(PersistentData.instance.getNations().entrySet());
        nations.sort((o1, o2) -> o2.getValue().getPower().getTotal() - o1.getValue().getPower().getTotal());

        // Create the builder with the message title.
        ComponentBuilder builder = new ComponentBuilder(Language.format("nationList",
                new String[]{"pageStart", Integer.toString(pageStart + 1)},
                new String[]{"pageEnd", Integer.toString(pageEnd)},
                new String[]{"nationCount", Integer.toString(nationCount)}
        ) + "\n \n");

        // Add all of the entries.
        for (int i = pageStart; i < pageEnd; i++) {
            TextComponent component = new TextComponent(Language.format("nationListEntry",
                    new String[]{"nationName", nations.get(i).getValue().getName()},
                    new String[]{"nationPower", Integer.toString(nations.get(i).getValue().getPower().getTotal())},
                    new String[]{"nationClaimThreshold", Integer.toString(nations.get(i).getValue().getPower().getClaimThreshold())},
                    new String[]{"nationExistenceThreshold", Integer.toString(nations.get(i).getValue().getPower().getExistenceThreshold())}
            ) + "\n");
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation membersother " + nations.get(i).getKey().toString()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text(Language.format("nationListEntryTooltip", new String[]{"nationName", nations.get(i).getValue().getName()})
            )));

            builder.append(component);
        }

        // Add an extra newline between the bottom entry and the page buttons.
        builder.append(" \n", ComponentBuilder.FormatRetention.NONE);

        // Add the previous page button.
        if (page > 1) {
            TextComponent previousPageComponent = new TextComponent(Language.getLine("pagePrevious") + " ");
            previousPageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation list " + (page - 1)));
            previousPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.getLine("pagePreviousTooltip"))));
            builder.append(previousPageComponent, ComponentBuilder.FormatRetention.NONE);
        } else {
            TextComponent previousPageComponent = new TextComponent(Language.getLine("pagePreviousNone") + " ");
            previousPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.getLine("pagePreviousNoneTooltip"))));
            builder.append(previousPageComponent, ComponentBuilder.FormatRetention.NONE);
        }

        // Add the page number.
        builder.append(Language.format("page",
                new String[]{"page", Integer.toString(page)},
                new String[]{"pageMax", Integer.toString(pageMax)}
        ), ComponentBuilder.FormatRetention.NONE);

        // Add the next page button.
        if (page < pageMax) {
            TextComponent nextPageComponent = new TextComponent(" " + Language.getLine("pageNext"));
            nextPageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation list " + (page + 1)));
            nextPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.getLine("pageNextTooltip"))));
            builder.append(nextPageComponent, ComponentBuilder.FormatRetention.NONE);
        } else {
            TextComponent nextPageComponent = new TextComponent(" " + Language.getLine("pageNextNone"));
            nextPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.getLine("pageNextNoneTooltip"))));
            builder.append(nextPageComponent, ComponentBuilder.FormatRetention.NONE);
        }

        // Send the player the message.
        player.sendMessage(builder.create());
    }

    @Override
    public @Nullable ArrayList<String> getAutocomplete(Player player, Nation nation, NationMember member) {
        return new ArrayList<>(Arrays.asList("<page>"));
    }
}
