package com.willfp.eco.core.recipe.parts;

import com.willfp.eco.core.items.TestableItem;
import java.util.function.Predicate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Existing testable items with an extra filter.
 *
 * @see com.willfp.eco.core.items.CustomItem
 */
public class ModifiedTestableItem implements TestableItem {
    /**
     * The underlying item that must also match.
     */
    private final TestableItem handle;

    /**
     * The extra filter applied on top of the handle.
     */
    private final Predicate<ItemStack> test;

    /**
     * The item for the modified test.
     */
    private final ItemStack example;

    /**
     * Create a new modified testable item.
     *
     * @param item    The underlying item that must also match.
     * @param test    The extra filter applied on top of the underlying item.
     * @param example The example item returned by {@link #getItem()}.
     */
    public ModifiedTestableItem(@NotNull final TestableItem item,
                                @NotNull final Predicate<ItemStack> test,
                                @NotNull final ItemStack example) {
        this.handle = item;
        this.test = test;
        this.example = example;
    }

    /**
     * If the item matches both the underlying item and the extra filter.
     *
     * @param itemStack The item to test.
     * @return If the item is non-null, matches the handle, and passes the extra filter.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && handle.matches(itemStack) && test.test(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return example;
    }

    /**
     * Get the handle.
     *
     * @return The underlying item.
     */
    public TestableItem getHandle() {
        return this.handle;
    }

    /**
     * Get the test.
     *
     * @return The extra filter.
     */
    public Predicate<ItemStack> getTest() {
        return this.test;
    }
}
