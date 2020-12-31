package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ClaimBlock {
    private static final NamespacedKey nbtKey = new NamespacedKey(AscendNations.getInstance(), ClaimBlockMetadata.key);

    private static ItemStack homeBlock;
    private static ItemStack outpostBlock;

    public static boolean placeHome(Block block, Player player, Nation nation) {
        if (nation.getHome() != null) {
            player.sendMessage(Language.getLine("errorClaimHomeAlreadyClaimed"));
            return false;
        }

        ClaimChunks.claim(nation, block.getLocation());
        nation.setHome(block.getBlockKey());

        block.setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Home));

        player.sendMessage(Language.getLine("claimHome"));
        return true;
    }

    public static boolean placeOutpost(Block block, Player player, Nation nation) {
        if (nation.getHome() == null) {
            player.sendMessage(Language.getLine("errorClaimOutpostNoHome"));
            return false;
        }

        if (!nation.hasOutpostClaims()) {
            player.sendMessage(Language.format("errorClaimOutpostMaxClaimed", new String[]{"outpostCount", Integer.toString(nation.getPower().getOutpostsClaimable())}));
            return false;
        }

        ClaimChunks.claim(nation, block.getLocation());
        nation.getOutposts().add(block.getBlockKey());

        block.setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Outpost));

        player.sendMessage(Language.format("claimOutpost",
                new String[]{"outpostsClaimed", Integer.toString(nation.getOutposts().size())},
                new String[]{"outpostsCap", Integer.toString(nation.getPower().getOutpostsClaimable())}
        ));
        return true;
    }

    public static void giveHome(Player player) {
        if (homeBlock == null) {
            homeBlock = new ItemStack(NationVariables.instance.getClaimBlockHome());

            ItemMeta meta = homeBlock.getItemMeta();
            meta.setDisplayName(Language.getLine("blockHomeName"));
            meta.setLore(Arrays.asList(Language.getLine("blockHomeLore").split("\n")));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(nbtKey, PersistentDataType.INTEGER, ClaimBlockType.Home.ordinal());

            homeBlock.setItemMeta(meta);
        }

        give(player, homeBlock);
    }

    public static void giveOutpost(Player player) {
        if (outpostBlock == null) {
            outpostBlock = new ItemStack(NationVariables.instance.getClaimBlockOutpost());

            ItemMeta meta = outpostBlock.getItemMeta();
            meta.setDisplayName(Language.getLine("blockOutpostName"));
            meta.setLore(Arrays.asList(Language.getLine("blockOutpostLore").split("\n")));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(nbtKey, PersistentDataType.INTEGER, ClaimBlockType.Outpost.ordinal());

            outpostBlock.setItemMeta(meta);
        }

        give(player, outpostBlock);
    }

    private static void give(Player player, ItemStack block) {
        // TODO: check meta instead of type.
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

    public static boolean isClaimBlock(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(nbtKey, PersistentDataType.INTEGER);
    }

    public static boolean isClaimBlock(Block block) {
        return block.hasMetadata(ClaimBlockMetadata.key);
    }

    public static void removedBlock(Player player, ItemStack block) {
        player.sendMessage(Language.format("removeBlock", new String[]{"blockName", block.getItemMeta().getDisplayName()}));
    }
}
