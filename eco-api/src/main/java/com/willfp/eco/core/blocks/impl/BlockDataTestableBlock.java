package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.Blocks;
import com.willfp.eco.core.blocks.TestableBlock;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A testable block for vanilla materials.
 */
public class BlockDataTestableBlock implements TestableBlock {
    /**
     * The block type.
     */
    private final BlockData blockData;

    /**
     * Create a new block data testable block.
     *
     * @param material The material.
     */
    public BlockDataTestableBlock(@NotNull final Material material) {
        this(material.createBlockData());
    }

    /**
     * Create a new block data testable block.
     *
     * @param blockData The material.
     */
    public BlockDataTestableBlock(@NotNull final BlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public boolean matches(@Nullable final Block block) {
        boolean simpleMatches = block != null && block.getBlockData().equals(blockData);

        if (!simpleMatches) {
            return false;
        }

        return !Blocks.isCustomBlock(block);
    }

    @Override
    public @NotNull Block place(@NotNull Location location) {
        Validate.notNull(location.getWorld());

        Block block = location.getBlock();
        block.setBlockData(blockData);

        return block;
    }

    /**
     * Get the block data.
     *
     * @return The block data.
     */
    public BlockData getBlockData() {
        return this.blockData;
    }
}
