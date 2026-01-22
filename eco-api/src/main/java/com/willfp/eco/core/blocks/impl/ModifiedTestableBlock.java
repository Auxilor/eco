package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.TestableBlock;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Existing testable block with an extra function.
 *
 * @see com.willfp.eco.core.entities.CustomEntity
 */
public class ModifiedTestableBlock implements TestableBlock {
    /**
     * The block.
     */
    private final TestableBlock handle;

    /**
     * The data.
     */
    private final Predicate<Block> test;

    /**
     * The provider to place a block.
     */
    private final Function<Location, Block> provider;

    /**
     * Create a new modified testable block.
     *
     * @param block    The base block.
     * @param test     The test.
     * @param provider The provider to place a block.
     */
    public ModifiedTestableBlock(@NotNull final TestableBlock block,
                                 @NotNull final Predicate<@NotNull Block> test,
                                 @NotNull final Function<Location, Block> provider) {
        this.handle = block;
        this.test = test;
        this.provider = provider;
    }

    @Override
    public boolean matches(@Nullable final Block block) {
        return block != null && handle.matches(block) && test.test(block);
    }

    @Override
    public @NotNull Block place(@NotNull final Location location) {
        Validate.notNull(location.getWorld());

        return provider.apply(location);
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
