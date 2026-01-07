package com.willfp.eco.core.blocks;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A block and its hash.
 */
public final class HashedBlock {
    /**
     * The block.
     */
    private final Block block;

    /**
     * The hash.
     */
    private final int hash;

    /**
     * Create new hashed block.
     *
     * @param block The block.
     * @param hash The hash.
     */
    private HashedBlock(@NotNull final Block block,
                        final int hash) {
        this.block = block;
        this.hash = hash;
    }

    /**
     * Get the block.
     *
     * @return The Block.
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * Get the hash.
     *
     * @return The hash.
     */
    public int getHash() {
        return this.hash;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (!(other instanceof HashedBlock o)) {
            return false;
        }

        return o.hash == this.hash;
    }

    /**
     * Hashed block from a block.
     *
     * @param block The block.
     * @return The hashed block.
     */
    public static HashedBlock of(@NotNull final Block block) {
        return new HashedBlock(block, Objects.hash(block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
    }
}
