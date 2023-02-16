package com.willfp.eco.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilities / API methods for blocks.
 */
public final class BlockUtils {
    /**
     * Max blocks to mine (yes, this is to prevent a stack overflow).
     */
    private static final int MAX_BLOCKS = 2500;

    private static Set<Block> getNearbyBlocks(@NotNull final Block start,
                                              @NotNull final List<Material> allowedMaterials,
                                              @NotNull final Set<Block> blocks,
                                              final int limit) {
        for (BlockFace face : BlockFace.values()) {
            Block block = start.getRelative(face);
            if (blocks.contains(block)) {
                continue;
            }

            if (allowedMaterials.contains(block.getType())) {
                blocks.add(block);

                if (blocks.size() > limit || blocks.size() > MAX_BLOCKS) {
                    return blocks;
                }

                blocks.addAll(getNearbyBlocks(block, allowedMaterials, blocks, limit));
            }
        }

        return blocks;
    }

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
        return getNearbyBlocks(start, allowedMaterials, new HashSet<>(), limit);
    }

    /**
     * Break the block as if the player had done it manually.
     *
     * @param player The player to break the block as.
     * @param block  The block to break.
     * @deprecated Added into spigot API in 1.17.1
     */
    @Deprecated(since = "6.26.2", forRemoval = true)
    public static void breakBlock(@NotNull final Player player,
                                  @NotNull final Block block) {
        Location location = block.getLocation();
        World world = location.getWorld();
        assert world != null;

        if (location.getY() < world.getMinHeight() || location.getY() > world.getMaxHeight()) {
            return;
        }

        player.breakBlock(block);
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
    /**
     * Get who placed the block (as an internal ID).
     *
     * @param block The block.
     * @return Internal ID of placing player, or 0 if not placed by a player.
     */
    public static int placedByID(@NotNull final Block block) {
        Chunk chunk = block.getChunk();

        return chunk.getPersistentDataContainer().getOrDefault(
            NamespacedKeyUtils.createEcoKey(Integer.toString(block.getLocation().hashCode(), 16)),
            PersistentDataType.INTEGER,
            0
        );
    }

    /**
     * Get whether a specific player placed the block.
     *
     * @param block The block.
     * @param player The player to check.
     * @return If placed by that specific player.
     */
    public static boolean isPlacedBy(@NotNull final Block block, @NotNull final OfflinePlayer player) {
        int placed_by = placedByID(block);
        int local_id = PlayerUtils.getLocalID(player);
        return placed_by == local_id;        
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
