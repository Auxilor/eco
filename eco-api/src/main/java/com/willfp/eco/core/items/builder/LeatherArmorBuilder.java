package com.willfp.eco.core.items.builder;

import com.willfp.eco.internal.items.AbstractItemStackBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class LeatherArmorBuilder extends AbstractItemStackBuilder<LeatherArmorMeta, LeatherArmorBuilder> {
    /**
     * Create a new ItemStackBuilder.
     *
     * @param material The material.
     */
    public LeatherArmorBuilder(@NotNull final Material material) {
        super(material);
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param base The ItemStack to start with.
     */
    public LeatherArmorBuilder(@NotNull final ItemStack base) {
        super(base);
    }

    /**
     * Set leather color.
     *
     * @param color The color.
     * @return The builder.
     */
    public LeatherArmorBuilder setColor(@NotNull final java.awt.Color color) {
        Color bukkitColor = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        this.getMeta().setColor(bukkitColor);

        return this;
    }
}
