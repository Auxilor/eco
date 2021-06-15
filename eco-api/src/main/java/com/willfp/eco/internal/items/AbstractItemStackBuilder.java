package com.willfp.eco.internal.items;

import com.willfp.eco.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractItemStackBuilder<T extends ItemMeta> {
    /**
     * The ItemMeta used while building.
     */
    @Getter(AccessLevel.PROTECTED)
    private final T meta;

    /**
     * The ItemStack.
     */
    @Getter(AccessLevel.PROTECTED)
    private final ItemStack base;

    /**
     * Create a new ItemStackBuilder.
     *
     * @param material The material.
     */
    protected AbstractItemStackBuilder(@NotNull final Material material) {
        this(new ItemStack(material));
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param base The ItemStack to start with.
     */
    protected AbstractItemStackBuilder(@NotNull final ItemStack base) {
        this.base = base;
        this.meta = (T) base.getItemMeta();
        assert meta != null;
    }

    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> setAmount(final int amount) {
        Validate.isTrue(amount >= 1 && amount <= base.getMaxStackSize());
        base.setAmount(amount);
        return this;
    }

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> addEnchantment(@NotNull final Enchantment enchantment,
                                                      final int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Set the item display name.
     *
     * @param name The name.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> setDisplayName(@NotNull final String name) {
        meta.setDisplayName(StringUtils.translate(name));
        return this;
    }

    /**
     * Add lore line.
     *
     * @param line The line.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> addLoreLine(@NotNull final String line) {
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        lore.add(StringUtils.translate(line));
        meta.setLore(lore);

        return this;
    }

    /**
     * Add lore lines.
     *
     * @param lines The lines.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> addLoreLines(@NotNull final List<String> lines) {
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        for (String line : lines) {
            lore.add(StringUtils.translate(line));
        }
        meta.setLore(lore);

        return this;
    }

    /**
     * Add ItemFlags.
     *
     * @param itemFlags The flags.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> addItemFlag(@NotNull final ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);

        return this;
    }

    /**
     * Set unbreakable.
     *
     * @param unbreakable If the item should be unbreakable.
     * @return The builder.
     */
    public AbstractItemStackBuilder<T> setUnbreakable(final boolean unbreakable) {
        meta.setUnbreakable(unbreakable);

        return this;
    }

    /**
     * Build the item.
     *
     * @return The item.
     */
    public ItemStack build() {
        base.setItemMeta(meta);

        return base;
    }
}
