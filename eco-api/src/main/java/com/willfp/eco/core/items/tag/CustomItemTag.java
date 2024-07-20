package com.willfp.eco.core.items.tag;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A custom item tag.
 */
public final class CustomItemTag implements ItemTag {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * The test.
     */
    private final Predicate<@NotNull ItemStack> test;

    /**
     * The example item.
     */
    private final Supplier<@NotNull ItemStack> exampleItem;

    /**
     * Create a new custom item tag.
     *
     * @param key  The key.
     * @param test The test.
     */
    public CustomItemTag(@NotNull final NamespacedKey key,
                         @NotNull final Predicate<@NotNull ItemStack> test) {
        this(
                key,
                test,
                () -> new ItemStack(Material.STONE)
        );
    }

    /**
     * Create a new custom item tag with an example item.
     *
     * @param key         The key.
     * @param test        The test.
     * @param exampleItem The example item.
     */
    public CustomItemTag(@NotNull final NamespacedKey key,
                         @NotNull final Predicate<@NotNull ItemStack> test,
                         @NotNull final Supplier<@NotNull ItemStack> exampleItem) {
        this.key = key;
        this.test = test;
        this.exampleItem = exampleItem;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return key.toString();
    }

    @Override
    public boolean matches(@NotNull final ItemStack itemStack) {
        return test.test(itemStack);
    }

    @Override
    @NotNull
    public ItemStack getExampleItem() {
        return exampleItem.get();
    }
}
