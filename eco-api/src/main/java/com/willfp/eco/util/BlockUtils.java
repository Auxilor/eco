package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Utilities / API methods for blocks.
 */
@UtilityClass
public class BlockUtils {
    /**
     * If the meta set function has been set.
     */
    private boolean initialized = false;

    /**
     * The block break function.
     */
    private BiConsumer<Player, Block> blockBreakConsumer = null;

    private Set<Block> getNearbyBlocks(@NotNull final Block start,
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

                if (blocks.size() > limit || blocks.size() > 2500) {
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
    public Set<Block> getVein(@NotNull final Block start,
                              @NotNull final List<Material> allowedMaterials,
                              final int limit) {
        return getNearbyBlocks(start, allowedMaterials, new HashSet<>(), limit);
    }

    /**
     * Break the block as if the player had done it manually.
     *
     * @param player The player to break the block as.
     * @param block  The block to break.
     */
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(blockBreakConsumer, "Must be initialized!");

        Location location = block.getLocation();
        World world = location.getWorld();
        assert world != null;

        if (location.getY() < world.getMinHeight() || location.getY() > world.getMaxHeight()) {
            return;
        }

        blockBreakConsumer.accept(player, block);
    }

    /**
     * Get if a block was placed by a player.
     *
     * @param block The block.
     * @return If placed by a player.
     */
    public boolean isPlayerPlaced(@NotNull final Block block) {
        Chunk chunk = block.getChunk();

        return chunk.getPersistentDataContainer().has(
                NamespacedKeyUtils.createEcoKey(Integer.toString(block.getLocation().hashCode(), 16)),
                PersistentDataType.INTEGER
        );
    }

    /**
     * Initialize the block break function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public void initialize(@NotNull final BiConsumer<Player, Block> function) {
        Validate.isTrue(!initialized, "Already initialized!");

        blockBreakConsumer = function;

        initialized = true;
    }
}
