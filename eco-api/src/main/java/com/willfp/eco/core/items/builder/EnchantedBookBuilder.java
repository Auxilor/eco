package com.willfp.eco.core.items.builder;

import com.willfp.eco.internal.items.AbstractItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

public class EnchantedBookBuilder extends AbstractItemStackBuilder<EnchantmentStorageMeta, EnchantedBookBuilder> {
    /**
     * Create a new EnchantedBookBuilder.
     */
    public EnchantedBookBuilder() {
        super(Material.ENCHANTED_BOOK);
    }

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.
     */
    public EnchantedBookBuilder addStoredEnchantment(@NotNull final Enchantment enchantment,
                                                     final int level) {
        this.getMeta().addStoredEnchant(enchantment, level, true);
        return this;
    }
}
