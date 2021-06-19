package com.willfp.eco.core.items.builder;

import com.willfp.eco.internal.items.AbstractItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
}
