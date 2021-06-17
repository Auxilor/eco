package com.willfp.eco.core.items.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface ItemBuilder<T extends ItemMeta, U extends ItemBuilder<T, U>> {
    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount.
     * @return The builder.
     */
    U setAmount(int amount);

    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount.
     * @return The builder.
     */
    U setAmount(@NotNull Supplier<Integer> amount);

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.r
     */
    U addEnchantment(@NotNull Enchantment enchantment,
                     int level);

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.
     */
    U addEnchantment(@NotNull Supplier<Enchantment> enchantment,
                     @NotNull Supplier<Integer> level);

    /**
     * Set the item display name.
     *
     * @param name The name.
     * @return The builder.
     */
    U setDisplayName(@NotNull String name);

    /**
     * Set the item display name.
     *
     * @param name The name.
     * @return The builder.
     */
    U setDisplayName(@NotNull Supplier<String> name);

    /**
     * Add lore line.
     *
     * @param line The line.
     * @return The builder.
     */
    U addLoreLine(@NotNull String line);

    /**
     * Add lore line.
     *
     * @param line The line.
     * @return The builder.
     */
    U addLoreLine(@NotNull Supplier<String> line);

    /**
     * Add lore lines.
     *
     * @param lines The lines.
     * @return The builder.
     */
    U addLoreLines(@NotNull List<String> lines);

    /**
     * Add lore lines.
     *
     * @param lines The lines.
     * @return The builder.
     */
    U addLoreLines(@NotNull Supplier<List<String>> lines);

    /**
     * Add ItemFlags.
     *
     * @param itemFlags The flags.
     * @return The builder.
     */
    U addItemFlag(@NotNull ItemFlag... itemFlags);

    /**
     * Add ItemFlags.
     *
     * @param itemFlags The flags.
     * @return The builder.
     */
    U addItemFlag(@NotNull Supplier<ItemFlag[]> itemFlags);

    /**
     * Write meta key.
     *
     * @param key   The key.
     * @param type  The type.
     * @param value The value.
     * @param <A>   The type.
     * @param <B>   The type.
     * @return The builder.
     */
    <A, B> U writeMetaKey(@NotNull NamespacedKey key,
                          @NotNull PersistentDataType<A, B> type,
                          @NotNull B value);

    /**
     * Write meta key.
     *
     * @param key   The key.
     * @param type  The type.
     * @param value The value.
     * @param <A>   The type.
     * @param <B>   The type.
     * @return The builder.
     */
    <A, B> U writeMetaKey(@NotNull Supplier<NamespacedKey> key,
                          @NotNull Supplier<PersistentDataType<A, B>> type,
                          @NotNull Supplier<B> value);

    /**
     * Set unbreakable.
     *
     * @param unbreakable If the item should be unbreakable.
     * @return The builder.
     */
    U setUnbreakable(boolean unbreakable);

    /**
     * Set unbreakable.
     *
     * @param unbreakable If the item should be unbreakable.
     * @return The builder.
     */
    U setUnbreakable(@NotNull Supplier<Boolean> unbreakable);

    /**
     * Set custom model data.
     *
     * @param data The data.
     * @return The builder.
     */
    U setCustomModelData(@Nullable Integer data);

    /**
     * Set custom model data.
     *
     * @param data The data.
     * @return The builder.
     */
    U setCustomModelData(@NotNull Supplier<Integer> data);

    /**
     * Build the item.
     *
     * @return The item.
     */
    ItemStack build();
}
