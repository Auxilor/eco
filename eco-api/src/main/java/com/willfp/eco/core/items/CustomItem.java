package com.willfp.eco.core.items;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CustomItem implements TestableItem {
    /**
     * The test for ItemStacks to pass.
     */
    @Getter
    private final Predicate<ItemStack> predicate;

    /**
     * Example Item: what the user should see.
     */
    private final ItemStack item;

    /**
     * Create a new complex recipe part.
     * @param predicate The test.
     * @param item The example ItemStacks.
     */
    public CustomItem(@NotNull final Predicate<ItemStack> predicate,
                      @NotNull final ItemStack item) {
        this.predicate = predicate;
        this.item = item;
    }

    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return predicate.test(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}
