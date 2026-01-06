package com.willfp.eco.core.blocks.tag;

import com.willfp.eco.core.blocks.TestableBlock;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A group of items that share a common trait.
 */
public interface BlockTag {
    /**
     * Get the identifier of the tag.
     *
     * @return The identifier.
     */
    @NotNull
    String getIdentifier();

    /**
     * Check if an block matches the tag.
     *
     * @param block The block to check.
     * @return If the block matches the tag.
     */
    boolean matches(@NotNull Block block);

    /**
     * Convert this tag to a testable item.
     *
     * @return The testable item.
     */
    @NotNull
    default TestableBlock toTestableBlock() {
        return new TestableBlock() {
            @Override
            public boolean matches(@Nullable final Block block) {
                return block != null && BlockTag.this.matches(block);
            }

            @Override
            public @NotNull Block place(@NotNull final Location location) {
                return location.getBlock();
            }

            @Override
            public String toString() {
                return "BlockTagTestableBlock{" +
                        "tag=" + BlockTag.this.getIdentifier() +
                        '}';
            }
        };
    }
}
