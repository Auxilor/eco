package com.willfp.eco.core.recipe.parts;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stacks of items.
 * <p>
 * Wraps another {@link TestableItem} and additionally requires the tested stack to have
 * at least a given amount.
 */
public class TestableStack implements TestableItem {
    /**
     * The underlying item that must also match.
     */
    private final TestableItem handle;

    /**
     * The minimum amount required.
     */
    private final int amount;

    /**
     * Create a new testable stack.
     *
     * @param item   The underlying item. Cannot itself be a {@link TestableStack} or an
     *               {@link EmptyTestableItem}.
     * @param amount The minimum amount.
     * @throws IllegalArgumentException If the item is a {@link TestableStack} or an
     *                                  {@link EmptyTestableItem}.
     */
    public TestableStack(@NotNull final TestableItem item,
                         final int amount) {
        Preconditions.checkArgument(!(item instanceof TestableStack), "You can't stack a stack!");
        Preconditions.checkArgument(!(item instanceof EmptyTestableItem), "You can't stack air!");

        this.handle = item;
        this.amount = amount;
    }

    /**
     * If the item matches the underlying item and has at least the required amount.
     *
     * @param itemStack The item to test.
     * @return If the item is non-null, matches the handle, and has an amount of at least
     *         {@link #getAmount()}.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && handle.matches(itemStack) && itemStack.getAmount() >= amount;
    }

    @Override
    public ItemStack getItem() {
        ItemStack temp = handle.getItem().clone();
        temp.setAmount(amount);
        return temp;
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
     * Get the minimum amount required in the stack.
     *
     * @return The amount.
     */
    public int getAmount() {
        return this.amount;
    }
}
