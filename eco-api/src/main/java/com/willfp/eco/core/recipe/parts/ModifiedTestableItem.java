package com.willfp.eco.core.recipe.parts;

import com.willfp.eco.core.items.TestableItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Existing testable items with an extra filter.
 *
 * @see com.willfp.eco.core.items.CustomItem
 */
public class ModifiedTestableItem implements TestableItem {
    /**
     * The item.
     */
    @Getter
    private final TestableItem handle;

    /**
     * The amount.
     */
    @Getter
    private final Predicate<ItemStack> test;

    /**
     * The item for the modified test.
     */
    private final ItemStack example;

    /**
     * Create a new modified testable item.
     *
     * @param item    The item.
     * @param test    The test.
     * @param example The example.
     */
    public ModifiedTestableItem(@NotNull final TestableItem item,
                                @NotNull final Predicate<ItemStack> test,
                                @NotNull final ItemStack example) {
        this.handle = item;
        this.test = test;
        this.example = example;
    }

    /**
     * If the item matches the material.
     *
     * @param itemStack The item to test.
     * @return If the item is of the specified material.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && handle.matches(itemStack) && test.test(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return example;
    }
}
