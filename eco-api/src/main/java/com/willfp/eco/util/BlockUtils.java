package com.willfp.eco.util;

import com.willfp.eco.core.blocks.TestableBlock;
import java.util.*;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for blocks.
 */
public final class BlockUtils {
    /**
     * Get a set of all blocks in contact with each other of a specific type.
     * <p>
     * The search is a flood fill starting at the given block, and treats every {@link BlockFace}
     * value as adjacent, so diagonally touching blocks are included in the vein.
     *
     * @param start         The initial block, which is only included if it matches one of the allowed blocks.
     * @param allowedBlocks A list of all valid {@link TestableBlock}s.
     * @param limit         The maximum size of vein to return.
     * @return A set of all matching {@link Block}s, containing at most limit blocks,
     *         or empty if the initial block does not match.
     */
    @NotNull
    public static Set<Block> getVein(@NotNull final Block start,
                                     @NotNull final List<TestableBlock> allowedBlocks,
                                     final int limit) {
        Set<Block> blocks = new HashSet<>();
        Queue<Block> toProcess = new LinkedList<>();

        if (allowedBlocks.stream().anyMatch(testableBlock -> testableBlock.matches(start))) {
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

                if (!blocks.contains(adjacentBlock) &&
                        allowedBlocks.stream().anyMatch(testableBlock -> testableBlock.matches(adjacentBlock))) {
                    toProcess.add(adjacentBlock);
                }
            }
        }

        return blocks;
    }

    /**
     * Get if a block was placed by a player.
     * <p>
     * This reads a marker stored by eco in the persistent data container of the block's
     * {@link Chunk}, so it only reports blocks placed while eco was tracking them.
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