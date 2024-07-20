package com.willfp.eco.core.items.tag;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A group of items that share a common trait.
 */
public sealed interface ItemTag permits CustomItemTag, VanillaItemTag {
    /**
     * Get the identifier of the tag.
     *
     * @return The identifier.
     */
    @NotNull
    String getIdentifier();

    /**
     * Check if an item matches the tag.
     *
     * @param itemStack The item to check.
     * @return If the item matches the tag.
     */
    boolean matches(@NotNull ItemStack itemStack);

    /**
     * Get an example item.
     *
     * @return The example item.
     */
    @NotNull
    ItemStack getExampleItem();

    /**
     * Convert this tag to a testable item.
     *
     * @return The testable item.
     */
    @NotNull
    default TestableItem toTestableItem() {
        return new TestableItem() {
            @Override
            public boolean matches(@Nullable final ItemStack itemStack) {
                return itemStack != null && ItemTag.this.matches(itemStack);
            }

            @Override
            public @NotNull ItemStack getItem() {
                return ItemTag.this.getExampleItem();
            }

            @Override
            public String toString() {
                return "ItemTagTestableItem{" +
                        "tag=" + ItemTag.this.getIdentifier() +
                        '}';
            }
        };
    }
}
