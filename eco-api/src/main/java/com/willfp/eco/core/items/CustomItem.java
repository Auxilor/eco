package com.willfp.eco.core.items;

import com.willfp.eco.core.Eco;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A custom item has 3 components.
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

        /*
        This runs the next tick, because it's very likely that the test can't return true
        immediately after due to registration order; so eco waits until the item should be
        working in order to check.
         */
        Eco.get().getEcoPlugin().getScheduler().runLater(() -> {
            if (!matches(getItem())) {
                Bukkit.getLogger().severe("Item with key " + key + " is invalid!");
            }
        }, 1);
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
        return item.clone();
    }

    /**
     * Register the item.
     */
    public void register() {
        Items.registerCustomItem(this.getKey(), this);
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }
}
