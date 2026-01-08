package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.TestableBlock;
import com.willfp.eco.core.entities.TestableEntity;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Existing testable block with an extra filter.
 *
 * @see com.willfp.eco.core.entities.CustomEntity
 */
public class ModifiedTestableBlock implements TestableBlock {
    /**
     * The block.
     */
    private final TestableBlock handle;

    /**
     * The test.
     */
    private final Predicate<BlockData> test;

    /**
     * Create a new modified testable block.
     *
     * @param block   The base block.
     * @param test     The test.
     */
    public ModifiedTestableBlock(@NotNull final TestableBlock block,
                                 @NotNull final Predicate<@NotNull BlockData> test) {
        this.handle = block;
        this.test = test;
    }

    @Override
    public boolean matches(@Nullable final Block block) {
        return block != null && handle.matches(block) && test.test(block.getBlockData());
    }

    @Override
    public @NotNull Block place(@NotNull final Location location) {
        Validate.notNull(location.getWorld());

        return location.getBlock();
    }

    /**
     * Get the handle.
     *
     * @return The handle.
     */
    public TestableBlock getHandle() {
        return this.handle;
    }
}
