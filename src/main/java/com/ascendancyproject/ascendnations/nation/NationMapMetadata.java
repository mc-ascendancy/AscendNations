package com.ascendancyproject.ascendnations.nation;

import com.ascendancyproject.ascendnations.AscendNations;
import com.ascendancyproject.ascendnations.AscendNationsHelper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValueAdapter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class NationMapMetadata extends MetadataValueAdapter {
    public static final String key = "an-map";

    private ArrayList<Long> chunks;
    private ArrayList<Byte> chunkY;

    private long expiry;

    public NationMapMetadata(ArrayList<Long> chunks, ArrayList<Byte> chunkY) {
        super(AscendNations.getInstance());
        this.chunks = chunks;
        this.chunkY = chunkY;
        expiry = System.currentTimeMillis() + NationVariables.instance.getMapDuration();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiry;
    }

    public void resetBlocks(Player player) {
        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = player.getWorld().getChunkAt(chunks.get(i));

            if (!chunk.isLoaded())
                continue;

            Block zeroBlock = chunk.getBlock(0, Byte.toUnsignedInt(chunkY.get(i)), 0);

            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            packet.getSectionPositions().writeSafely(0, new BlockPosition(zeroBlock.getX() >> 4, Byte.toUnsignedInt(chunkY.get(i)) >> 4, zeroBlock.getZ() >> 4));

            ArrayList<WrappedBlockData> blocks = new ArrayList<>();
            ArrayList<Short> positions = new ArrayList<>();

            for (int j = 0; j < 16; j++) {
                Location location = zeroBlock.getLocation().add(6 + j / 4, 0, 6 + j % 4);
                blocks.add(WrappedBlockData.createData(location.getBlock().getBlockData()));
                positions.add(AscendNationsHelper.shortFromLocation(location));
            }

            for (int j = 0; j < 4; j++) {
                Location location = zeroBlock.getLocation().add(7 + j / 2, 1, 7 + j % 2);
                blocks.add(WrappedBlockData.createData(location.getBlock().getBlockData()));
                positions.add(AscendNationsHelper.shortFromLocation(location));

                location.add(0, 1 ,0);
                blocks.add(WrappedBlockData.createData(location.getBlock().getBlockData()));
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
    }

    @Override
    public @Nullable ArrayList<Long> value() {
        return chunks;
    }

    @Override
    public void invalidate() {
        chunks = null;
        chunkY = null;
        expiry = 0;
    }
}
