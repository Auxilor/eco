package com.willfp.eco.core.blocks;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

    /**
     * If a block matching this test should be marked as a custom block.
     * <p>
     * This is true by default for backwards compatibility reasons.
     *
     * @return If the block should be marked as custom.
     */
    default boolean shouldMarkAsCustom() {
        return true;
    }

    /**
     * The hardness of this block as defined by the custom block plugin, or -1 if unknown.
     * <p>
     * A return value of -1 means fall back to {@link org.bukkit.block.Block#getType()}'s
     * {@link org.bukkit.Material#getHardness()} for hardness comparisons.
     *
     * @return The hardness, or -1 if not provided.
     */
    default float hardness() {
        return -1f;
    }
}
