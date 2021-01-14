package com.ascendancyproject.ascendnations.nation.commands;

import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.ascendancyproject.ascendnations.NationCommand;
import com.ascendancyproject.ascendnations.NationCommandAnnotation;
import com.ascendancyproject.ascendnations.PlayerData;
import com.ascendancyproject.ascendnations.language.Language;
import com.ascendancyproject.ascendnations.nation.Nation;
import com.ascendancyproject.ascendnations.nation.NationMapMetadata;
import com.ascendancyproject.ascendnations.nation.NationMember;
import com.ascendancyproject.ascendnations.nation.NationVariables;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

@NationCommandAnnotation(
        name = "map",
        description = "View all of your nation's claims."
)
public class NationCommandMap extends NationCommand {
    @Override
    public void execute(@NotNull Player player, @NotNull PlayerData playerData, @Nullable Nation nation, @Nullable NationMember member, @NotNull String[] args) {
        if (player.hasMetadata(NationMapMetadata.key))
            ((NationMapMetadata) player.getMetadata(NationMapMetadata.key).get(0)).resetBlocks(player);

        ArrayList<Long> chunks = new ArrayList<>();
        ArrayList<Byte> chunkY = new ArrayList<>();

        for (Long key : nation.getChunks()) {
            Chunk chunk = player.getWorld().getChunkAt(key);

            if (!chunk.isLoaded())
                continue;

            Block zeroBlock = chunk.getBlock(0, 0, 0);

            int minY = 0;
            for (int i = 0; i < 16; i++)
                minY = Math.max(minY, player.getWorld().getHighestBlockYAt(zeroBlock.getX() + 6 + i / 4, zeroBlock.getZ() + 6 + i % 4));

            minY = Math.min(253, minY + NationVariables.instance.getMapBeaconLevitation());
            if (minY % 16 >= 14)
                minY += 16 - minY % 16;

            chunks.add(key);
            chunkY.add((byte) minY);

            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            packet.getSectionPositions().writeSafely(0, new BlockPosition(zeroBlock.getX() >> 4, minY >> 4, zeroBlock.getZ() >> 4));

            ArrayList<WrappedBlockData> blocks = new ArrayList<>();
            ArrayList<Short> positions = new ArrayList<>();

            WrappedBlockData baseBlockData = WrappedBlockData.createData(NationVariables.instance.getMapBeaconMaterial());
            WrappedBlockData beaconBlockData = WrappedBlockData.createData(Material.BEACON);
            WrappedBlockData glassBlockData;

            if (key.equals(nation.getHomeChunk()))
                glassBlockData = WrappedBlockData.createData(NationVariables.instance.getMapBeaconGlassHomeMaterial());
            else if (nation.isClaimChunk(key))
                glassBlockData = WrappedBlockData.createData(NationVariables.instance.getMapBeaconGlassOutpostMaterial());
            else
                glassBlockData = WrappedBlockData.createData(NationVariables.instance.getMapBeaconGlassMaterial());

            for (int i = 0; i < 16; i++) {
                Location location = zeroBlock.getLocation().add(6 + i / 4, minY, 6 + i % 4);
                blocks.add(baseBlockData);
                positions.add(AscendNationsHelper.shortFromLocation(location));
            }

            for (int i = 0; i < 4; i++) {
                Location location = zeroBlock.getLocation().add(7 + i / 2, minY + 1, 7 + i % 2);
                blocks.add(beaconBlockData);
                positions.add(AscendNationsHelper.shortFromLocation(location));

                location.add(0, 1, 0);
                blocks.add(glassBlockData);
                positions.add(AscendNationsHelper.shortFromLocation(location));
            }

            packet.getBlockDataArrays().writeSafely(0, blocks.toArray(new WrappedBlockData[0]));
            packet.getShortArrays().writeSafely(0, ArrayUtils.toPrimitive(positions.toArray(new Short[0])));

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        player.setMetadata(NationMapMetadata.key, new NationMapMetadata(chunks, chunkY));

        Language.sendMessage(player, "nationMap");
    }
}
