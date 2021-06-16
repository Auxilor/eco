package com.willfp.eco.core.recipe.parts;

import com.willfp.eco.core.items.TestableItem;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestableStack implements TestableItem {
    /**
     * The item.
     */
    private final TestableItem item;

    /**
     * The amount.
     */
    @Getter
    private final int amount;

    /**
     * Create a new testable stack.
     *
     * @param item   The item.
     * @param amount The amount.
     */
    public TestableStack(@NotNull final TestableItem item,
                         final int amount) {
        Validate.isTrue(!(item instanceof TestableStack));

        this.item = item;
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
        return itemStack != null && item.matches(itemStack) && itemStack.getAmount() >= amount;
    }

    @Override
    public ItemStack getItem() {
        ItemStack temp = item.getItem().clone();
        temp.setAmount(amount);
        return temp;
    }
}
