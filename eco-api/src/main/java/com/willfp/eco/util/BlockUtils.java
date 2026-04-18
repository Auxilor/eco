package com.willfp.eco.util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Utilities / API methods for blocks.
 */
public final class BlockUtils {
    /**
     * Get a set of all blocks in contact with each other of a specific type.
     *
     * @param start            The initial block.
     * @param allowedMaterials A list of all valid {@link Material}s.
     * @param limit            The maximum size of vein to return.
     * @return A set of all {@link Block}s.
     */
    @NotNull
    public static Set<Block> getVein(@NotNull final Block start,
                                     @NotNull final List<Material> allowedMaterials,
                                     final int limit) {
        Set<Block> blocks = new HashSet<>();
        Queue<Block> toProcess = new LinkedList<>();

        if (allowedMaterials.contains(start.getType())) {
            toProcess.add(start);
        }

        while (!toProcess.isEmpty() && blocks.size() < limit) {
            Block currentBlock = toProcess.poll();

            if (blocks.contains(currentBlock)) {
                continue;
            }

            blocks.add(currentBlock);

            for (BlockFace face : BlockFace.values()) {
                Block adjacentBlock = currentBlock.getRelative(face);

                if (!blocks.contains(adjacentBlock) && allowedMaterials.contains(adjacentBlock.getType())) {
                    toProcess.add(adjacentBlock);
                }
            }
        }

        return blocks;
    }

    /**
     * Get if a block was placed by a player.
     *
     * @param block The block.
     * @return If placed by a player.
     */
    public static boolean isPlayerPlaced(@NotNull final Block block) {
        Chunk chunk = block.getChunk();

        return chunk.getPersistentDataContainer().has(
                NamespacedKeyUtils.createEcoKey(Integer.toString(block.getLocation().hashCode(), 16)),
                PersistentDataType.INTEGER
        );
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
