package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NationCommandAnnotation(
        name = "help",
        description = "Get help for the nation commands.",
        requiresNation = false
)
public class NationCommandHelp extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        TextComponent text = new TextComponent(Language.format("nationHelp", new String[]{"link", Language.getLine("nationHelpLinkDisplay")}));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Language.getLine("nationHelpTooltip"))));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Language.getLine("nationHelpLink")));

        player.sendMessage(text);
    }
}
