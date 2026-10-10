package com.willfp.eco.core.blocks;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    /**
     * The items this block drops when broken with a tool, as the custom block plugin rolls them.
     * <p>
     * Custom block plugins hand out their own drops when a player breaks a block, so anything
     * that breaks blocks without a player, like a minion, asks here instead of using
     * {@link Block#getDrops(ItemStack)}.
     *
     * @param block The block in the world, which must match this test.
     * @param tool  The tool used, or null for none.
     * @return The drops, or null if the block uses its vanilla drops.
     */
    @Nullable
    default List<ItemStack> getDrops(@NotNull final Block block,
                                     @Nullable final ItemStack tool) {
        return null;
    }
}
