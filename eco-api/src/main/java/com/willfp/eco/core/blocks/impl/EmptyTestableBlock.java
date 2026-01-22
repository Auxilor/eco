package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.TestableBlock;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Empty block.
 */
public class EmptyTestableBlock implements TestableBlock {
    /**
     * Create a new empty testable block.
     */
    public EmptyTestableBlock() {

    }

    @Override
    public boolean matches(@Nullable final Block other) {
        return false;
    }

    @Override
    public @NotNull Block place(@NotNull final Location location) {
        Validate.notNull(location.getWorld());

        return location.getBlock();
    }
}
