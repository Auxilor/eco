package com.willfp.eco.core.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface TestableItem {
    /**
     * If an ItemStack matches the recipe part.
     *
     * @param itemStack The item to test.
     * @return If the item matches.
     */
    boolean matches(@Nullable ItemStack itemStack);

    /**
     * Get an example item.
     *
     * @return The item.
     */
    ItemStack getItem();
}
