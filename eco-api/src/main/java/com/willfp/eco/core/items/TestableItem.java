package com.willfp.eco.core.items;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An item with a test to see if any item is that item.
 */
public interface TestableItem extends Testable<ItemStack> {
    /**
     * If an ItemStack matches the recipe part.
     *
     * @param itemStack The item to test.
     * @return If the item matches.
     */
    @Override
    boolean matches(@Nullable ItemStack itemStack);

    /**
     * Get an example item.
     *
     * @return The item.
     */
    ItemStack getItem();

    /**
     * If an item matching this test should be marked as a custom item.
     * <p>
     * This is true by default for backwards compatibility reasons.
     *
     * @return If the item should be marked as custom.
     */
    default boolean shouldMarkAsCustom() {
        return true;
    }
}
