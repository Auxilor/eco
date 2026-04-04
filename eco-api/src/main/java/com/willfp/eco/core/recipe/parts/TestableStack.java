package com.willfp.eco.core.recipe.parts;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stacks of items.
 */
public class TestableStack implements TestableItem {
    /**
     * The item.
     */
    private final TestableItem handle;

    /**
     * The amount.
     */
    private final int amount;

    /**
     * Create a new testable stack.
     *
     * @param item   The item.
     * @param amount The amount.
     */
    public TestableStack(@NotNull final TestableItem item,
                         final int amount) {
        Preconditions.checkArgument(!(item instanceof TestableStack), "You can't stack a stack!");
        Preconditions.checkArgument(!(item instanceof EmptyTestableItem), "You can't stack air!");

        this.handle = item;
        this.amount = amount;
    }

    /**
     * If the item matches the material.
     *
     * @param itemStack The item to test.
     * @return If the item is of the specified material.
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
     * @return The handle.
     */
    public TestableItem getHandle() {
        return this.handle;
    }

    /**
     * Get the amount in the stack.
     *
     * @return The amount.
     */
    public int getAmount() {
        return this.amount;
    }
}
