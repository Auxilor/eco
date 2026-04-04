package com.willfp.eco.core.recipe.parts;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A group of testable items.
 *
 * @see com.willfp.eco.core.items.CustomItem
 */
public class GroupedTestableItems implements TestableItem {
    /**
     * The children.
     */
    private final Collection<TestableItem> children;

    /**
     * Create a new group of testable items.
     *
     * @param children The children.
     */
    public GroupedTestableItems(@NotNull final Collection<TestableItem> children) {
        Preconditions.checkArgument(!children.isEmpty(), "Group must have at least one child!");

        this.children = children;
    }

    /**
     * If the item matches any children.
     *
     * @param itemStack The item to test.
     * @return If the item matches the test of any children..
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        for (TestableItem child : children) {
            if (child.matches(itemStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack getItem() {
        for (TestableItem child : children) {
            return child.getItem();
        }

        throw new IllegalStateException("Empty group of children!");
    }

    /**
     * Get matching child for an ItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The matching child, or null if the item matches nothing.
     */
    @Nullable
    public TestableItem getMatchingChild(@NotNull final ItemStack itemStack) {
        for (TestableItem child : children) {
            if (child.matches(itemStack)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Get the children.
     *
     * @return The children.
     */
    public Collection<TestableItem> getChildren() {
        return new ArrayList<>(children);
    }
}
