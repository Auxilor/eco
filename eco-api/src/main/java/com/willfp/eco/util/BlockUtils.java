package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.blocks.TestableBlock;
import java.util.*;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for blocks.
 */
public final class BlockUtils {
    private static final List<int[]> VEIN_OFFSETS = buildVeinOffsets();

    /**
     * Get a set of all blocks in contact with each other of a specific type.
     * <p>
     * The search is a flood fill starting at the given block. Every block touching another at a
     * face, an edge or a corner is adjacent, so diagonally touching blocks are included in the
     * vein. The horizontal offsets of every {@link BlockFace} value are adjacent as well.
     * <p>
     * On Folia the search stops at the boundary of the calling thread's region, so a vein
     * spanning two regions is returned only in part. Off Folia there is no such bound.
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

            for (int[] offset : VEIN_OFFSETS) {
                Block adjacentBlock = currentBlock.getRelative(offset[0], offset[1], offset[2]);

                // On Folia a vein can cross a region boundary, and reading a block in
                // another region is not allowed. Off Folia this is always owned, so the
                // fill is unbounded exactly as before.
                if (!Eco.get().isOwnedByCurrentRegion(adjacentBlock.getLocation())) {
                    continue;
                }

                if (!blocks.contains(adjacentBlock) &&
                        allowedBlocks.stream().anyMatch(testableBlock -> testableBlock.matches(adjacentBlock))) {
                    toProcess.add(adjacentBlock);
                }
            }
        }

        return blocks;
    }

    private static List<int[]> buildVeinOffsets() {
        Set<List<Integer>> offsets = new LinkedHashSet<>();

        for (BlockFace face : BlockFace.values()) {
            offsets.add(List.of(face.getModX(), face.getModY(), face.getModZ()));
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    offsets.add(List.of(x, y, z));
                }
            }
        }

        offsets.remove(List.of(0, 0, 0));

        return offsets.stream()
                .map(offset -> new int[]{offset.get(0), offset.get(1), offset.get(2)})
                .toList();
    }

    /**
     * Get if a block was placed by a player.
     * <p>
     * This reads a marker stored by eco in the persistent data container of the block's
     * {@link Chunk}, so it only reports blocks placed while eco was tracking them.
     * <p>
     * Markers are keyed by the block's position within its chunk. Markers written by older
     * versions of eco are keyed by the location hash, which several positions can share, and
     * are still read so that blocks placed before the update stay player-placed.
     *
     * @param block The block.
     * @return If placed by a player.
     */
    public static boolean isPlayerPlaced(@NotNull final Block block) {
        PersistentDataContainer container = block.getChunk().getPersistentDataContainer();

        return container.has(
                NamespacedKeyUtils.createEcoKey("placed_" + (block.getX() & 15) + "_" + block.getY() + "_" + (block.getZ() & 15)),
                PersistentDataType.INTEGER
        ) || container.has(
                NamespacedKeyUtils.createEcoKey(Integer.toString(block.getLocation().hashCode(), 16)),
                PersistentDataType.INTEGER
        );
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}