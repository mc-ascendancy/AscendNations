package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NationCommandAppoint extends NationCommand {
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, Nation nation, NationMember member, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Language.getLine("errorNationAppointBadUsername"));
            return;
        }

        OfflinePlayer appointed = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (appointed == null) {
            player.sendMessage(Language.format("errorNationNoPlayerFound", new String[]{"playerName", args[1]}));
            return;
        }

        if (appointed.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Language.format("errorCannotRunOnYourself", new String[]{"playerName", args[1]}));
            return;
        }

        NationMember appointedNationMember = nation.getMembers().get(appointed.getUniqueId());
        if (appointedNationMember == null) {
            player.sendMessage(Language.format("errorNationPlayerNotInNation", new String[]{"playerName", args[1]}));
            return;
        }

        member.setRole(NationRole.Commander);
        appointedNationMember.setRole(NationRole.Chancellor);

        player.sendMessage(Language.format("nationAppoint", new String[]{"appointedName", args[1]}));

        if (appointed.isOnline())
            ((Player) appointed).sendMessage(Language.format("nationAppointReceived", new String[]{"appointerName", player.getName()}));
    }

    public String getName() {
        return "appoint";
    }

    public String getDescription() {
        return "Appoint a new Chancellor for your nation.";
    }

    public String[] getAliases() {
        return new String[]{"appoint"};
    }

    @Override
    public NationRole minimumRole() {
        return NationRole.Chancellor;
    }
}
