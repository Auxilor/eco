package com.willfp.eco.core.items;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A custom item has 3 components:
 *
 * <ul>
 *     <li>The key to identify it</li>
 *     <li>The test to check if any item is this custom item</li>
 *     <li>The actual custom item {@link ItemStack}</li>
 * </ul>
 */
public class CustomItem implements TestableItem {
    /**
     * The key.
     */
    @Getter
    private final NamespacedKey key;

    /**
     * The test for ItemStacks to pass.
     */
    private final Predicate<@NotNull ItemStack> test;

    /**
     * Example Item: what the user should see.
     */
    private final ItemStack item;

    /**
     * Create a new complex recipe part.
     *
     * @param key  The item key.
     * @param test The test.
     * @param item The example ItemStacks.
     */
    public CustomItem(@NotNull final NamespacedKey key,
                      @NotNull final Predicate<@NotNull ItemStack> test,
                      @NotNull final ItemStack item) {
        this.key = key;
        this.test = test;
        this.item = item;

        Validate.isTrue(matches(getItem()), "The item must match the test!");
    }

    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return test.test(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    /**
     * Register the item.
     */
    public void register() {
        Items.registerCustomItem(this.getKey(), this);
    }
}
