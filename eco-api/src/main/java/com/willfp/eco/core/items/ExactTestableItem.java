package com.willfp.eco.core.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exact testable items use the default .equals() method.
 * This class is package-private as it more or less defeats the point of TestableItem.
 * It's only for NBT lookups.
 */
class ExactTestableItem implements TestableItem {
    /**
     * The ItemStack.
     */
    private final ItemStack item;

    /**
     * Create a new ExactTestableItem.
     *
     * @param itemStack The ItemStack.
     */
    ExactTestableItem(@NotNull final ItemStack itemStack) {
        this.item = itemStack;
    }

    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return item.equals(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return item.clone();
    }
}
