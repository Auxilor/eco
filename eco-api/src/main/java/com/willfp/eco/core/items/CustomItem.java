package com.willfp.eco.core.items;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CustomItem implements TestableItem {
    /**
     * The key.
     */
    @Getter
    private final NamespacedKey key;

    /**
     * The test for ItemStacks to pass.
     */
    private final Predicate<ItemStack> test;

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
                      @NotNull final Predicate<ItemStack> test,
                      @NotNull final ItemStack item) {
        this.key = key;
        this.test = test;
        this.item = item;
    }

    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
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
