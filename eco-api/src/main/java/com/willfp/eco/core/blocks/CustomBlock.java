package com.willfp.eco.core.blocks;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom block has 3 components.
 *
 * <ul>
 *     <li>The key to identify it</li>
 *     <li>The test to check if any block is this custom block</li>
 *     <li>The supplier to spawn the custom {@link Block}</li>
 * </ul>
 */
public class CustomBlock implements TestableBlock {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * The test for block to pass.
     */
    private final Predicate<@NotNull Block> test;

    /**
     * The provider to spawn the block.
     */
    private final Function<Location, Block> provider;

    /**
     * The hardness as defined by the custom block plugin, or -1 if unknown.
     */
    private final float hardness;

    /**
     * Create a new custom block.
     *
     * @param key      The block key.
     * @param test     The test.
     * @param provider The provider to spawn the block.
     * @param hardness The hardness, or -1 if not provided by the plugin.
     */
    public CustomBlock(@NotNull final NamespacedKey key,
                       @NotNull final Predicate<@NotNull Block> test,
                       @NotNull final Function<Location, Block> provider,
                       final float hardness) {
        this.key = key;
        this.test = test;
        this.provider = provider;
        this.hardness = hardness;
    }

    /**
     * Create a new custom block with unknown hardness.
     *
     * @param key      The block key.
     * @param test     The test.
     * @param provider The provider to spawn the block.
     */
    public CustomBlock(@NotNull final NamespacedKey key,
                       @NotNull final Predicate<@NotNull Block> test,
                       @NotNull final Function<Location, Block> provider) {
        this(key, test, provider, -1f);
    }

    @Override
    public boolean matches(@Nullable final Block other) {
        if (other == null) {
            return false;
        }

        return test.test(other);
    }

    @Override
    public float hardness() {
        return this.hardness;
    }

    @Override
    public @NotNull Block place(@NotNull final Location location) {
        Preconditions.checkNotNull(location.getWorld());

        return provider.apply(location);
    }

    /**
     * Register the block.
     */
    public void register() {
        Blocks.registerCustomBlock(this.getKey(), this);
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }
}
