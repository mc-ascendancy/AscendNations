package com.ascendancyproject.ascendnations.claim;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationOutpost;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import org.bukkit.Material;
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
            Language.sendMessage(player, "errorClaimHomeAlreadyClaimed");
            return false;
        }

        ClaimChunks.claim(nation, block.getChunk().getChunkKey());
        nation.setHome(block.getBlockKey());

        block.setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Home));

        Language.sendMessage(player, "claimHome");
        return true;
    }

    public static boolean placeOutpost(Block block, Player player, Nation nation) {
        if (nation.getHome() == null) {
            Language.sendMessage(player, "errorClaimOutpostNoHome");
            return false;
        }

        if (!nation.hasOutpostClaims()) {
            Language.sendMessage(player, "errorClaimOutpostMaxClaimed", new String[]{"outpostCount", Integer.toString(nation.getPower().getOutpostsClaimable())});
            return false;
        }

        ClaimChunks.claim(nation, block.getChunk().getChunkKey());
        int outpostNumber = new NationOutpost(nation, block.getBlockKey()).getNumber();

        block.setMetadata(ClaimBlockMetadata.key, new ClaimBlockMetadata(ClaimBlockType.Outpost));

        Language.sendMessage(player, "claimOutpost",
                new String[]{"outpostNumber", Integer.toString(outpostNumber)},
                new String[]{"outpostsClaimed", Integer.toString(nation.getOutposts().size())},
                new String[]{"outpostsCap", Integer.toString(nation.getPower().getOutpostsClaimable())}
        );
        return true;
    }

    public static void giveHome(Player player) {
        if (homeBlock == null) {
            homeBlock = new ItemStack(NationVariables.instance.getClaimBlockHome());

            ItemMeta meta = homeBlock.getItemMeta();
            meta.setDisplayName(Language.format(player, "blockHomeName"));
            meta.setLore(Arrays.asList(Language.format(player, "blockHomeLore").split("\n")));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(nbtKey, PersistentDataType.INTEGER, ClaimBlockType.Home.ordinal());

            homeBlock.setItemMeta(meta);
        }

        give(player, homeBlock, ClaimBlockType.Home);
    }

    public static void giveOutpost(Player player) {
        if (outpostBlock == null) {
            outpostBlock = new ItemStack(NationVariables.instance.getClaimBlockOutpost());

            ItemMeta meta = outpostBlock.getItemMeta();
            meta.setDisplayName(Language.format(player, "blockOutpostName"));
            meta.setLore(Arrays.asList(Language.format(player, "blockOutpostLore").split("\n")));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(nbtKey, PersistentDataType.INTEGER, ClaimBlockType.Outpost.ordinal());

            outpostBlock.setItemMeta(meta);
        }

        give(player, outpostBlock, ClaimBlockType.Outpost);
    }

    private static void give(Player player, ItemStack block, ClaimBlockType type) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getItemMeta().getPersistentDataContainer().has(nbtKey, PersistentDataType.INTEGER)) {
                ClaimBlockType foundType = ClaimBlockType.values()[itemStack.getItemMeta().getPersistentDataContainer().get(nbtKey, PersistentDataType.INTEGER)];

                if (type.equals(foundType)) {
                    Language.sendMessage(player, "errorGiveBlockAlreadyOwned", new String[]{"blockName", block.getItemMeta().getDisplayName()});
                    return;
                }
            }
        }

        if (!player.getInventory().addItem(block).isEmpty()) {
            // If the player's inventory is full:
            Language.sendMessage(player, "errorGiveBlockInventoryFull", new String[]{"blockName", block.getItemMeta().getDisplayName()});
        } else {
            Language.sendMessage(player, "giveBlock", new String[]{"blockName", block.getItemMeta().getDisplayName()});
        }
    }

    public static boolean isClaimBlock(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(nbtKey, PersistentDataType.INTEGER);
    }

    public static boolean isClaimBlock(Block block) {
        return block.hasMetadata(ClaimBlockMetadata.key);
    }

    public static void removedBlock(Player player, ItemStack block) {
        Language.sendMessage(player, "removeBlock", new String[]{"blockName", block.getItemMeta().getDisplayName()});
    }

    public static void removeBlock(Block block) {
        block.setType(Material.AIR);
        block.removeMetadata(ClaimBlockMetadata.key, AscendNations.getInstance());
    }
}
