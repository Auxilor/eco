package com.willfp.eco.core.items.builder;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for any item stack.
 */
public class ItemStackBuilder extends AbstractItemStackBuilder<ItemMeta, ItemStackBuilder> {
    /**
     * Create a new ItemStackBuilder.
     *
     * @param material The material.
     */
    public ItemStackBuilder(@NotNull final Material material) {
        super(material);
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param base The ItemStack to start with.
     */
    public ItemStackBuilder(@NotNull final ItemStack base) {
        super(base);
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param item The item to start with.
     */
    public ItemStackBuilder(@NotNull final TestableItem item) {
        super(item);
    }
}
