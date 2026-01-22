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
public class MaterialTestableBlock implements TestableBlock {
    /**
     * The block type.
     */
    protected final Material material;

    /**
     * Create a new block data testable block.
     *
     * @param material The material.
     */
    public MaterialTestableBlock(@NotNull final Material material) {
        this.material = material;
    }

    @Override
    public boolean matches(@Nullable final Block block) {
        boolean simpleMatches = block != null && block.getType() == material;

        if (!simpleMatches) {
            return false;
        }

        return !Blocks.isCustomBlock(block);
    }

    @Override
    public @NotNull Block place(@NotNull Location location) {
        Validate.notNull(location.getWorld());

        Block block = location.getBlock();
        block.setType(material);

        return block;
    }

    /**
     * Get the block data.
     *
     * @return The block data.
     */
    public Material getMaterial() {
        return this.material;
    }
}
