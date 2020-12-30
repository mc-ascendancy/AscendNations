package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ClaimBlock {
    private static ItemStack homeBlock;

    public static void giveHome(Player player) {
        if (homeBlock == null) {
            homeBlock = new ItemStack(NationVariables.instance.getClaimBlockHome());

            ItemMeta meta = homeBlock.getItemMeta();
            meta.setDisplayName(Language.getLine("blockHomeName"));
            meta.setLore(Arrays.asList(Language.getLine("blockHomeLore").split("\n")));
            homeBlock.setItemMeta(meta);
        }

        give(player, homeBlock);
    }

    private static void give(Player player, ItemStack block) {
        if (player.getInventory().contains(block)) {
            player.sendMessage(Language.format("errorGiveBlockAlreadyOwned", new String[]{"blockName", block.getItemMeta().getDisplayName()}));
            return;
        }

        if (!player.getInventory().addItem(block).isEmpty()) {
            // If the player's inventory is full:
            player.sendMessage(Language.format("errorGiveBlockInventoryFull", new String[]{"blockName", block.getItemMeta().getDisplayName()}));
        } else {
            player.sendMessage(Language.format("giveBlock", new String[]{"blockName", block.getItemMeta().getDisplayName()}));
        }
    }

    public static boolean isClaimBlock(Material material) {
        return material == NationVariables.instance.getClaimBlockHome();
    }

    public static void removedBlock(Player player, ItemStack block) {
        player.sendMessage(Language.format("removeBlock", new String[]{"blockName", block.getItemMeta().getDisplayName()}));
    }
}
