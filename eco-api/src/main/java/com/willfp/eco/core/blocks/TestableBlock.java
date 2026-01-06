package com.willfp.eco.core.blocks;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A block with a test.
 */
public interface TestableBlock extends Testable<Block> {
    /**
     * If a Block matches the test.
     *
     * @param other The other block.
     * @return If the block matches.
     */
    @Override
    boolean matches(@Nullable Block other);

    /**
     * Place the block.
     *
     * @param location The location.
     * @return The block.
     */
    @NotNull
    Block place(@NotNull Location location);
}
