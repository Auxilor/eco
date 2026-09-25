package com.willfp.eco.core.items.builder;

import com.willfp.eco.core.config.interfaces.Config;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for all item builders.
 */
public interface ItemBuilder {
    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount.
     * @return The builder.
     */
    ItemBuilder setAmount(int amount);

    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount.
     * @return The builder.
     */
    ItemBuilder setAmount(@NotNull Supplier<Integer> amount);

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.
     */
    ItemBuilder addEnchantment(@NotNull Enchantment enchantment,
                               int level);

    /**
     * Add an enchantment to the item.
     *
     * @param enchantment The enchantment.
     * @param level       The level.
     * @return The builder.
     */
    ItemBuilder addEnchantment(@NotNull Supplier<Enchantment> enchantment,
                               @NotNull Supplier<Integer> level);

    /**
     * Set the item display name.
     *
     * @param name The name.
     * @return The builder.
     */
    ItemBuilder setDisplayName(@NotNull String name);

    /**
     * Set the item display name.
     *
     * @param name The name.
     * @return The builder.
     */
    ItemBuilder setDisplayName(@NotNull Supplier<String> name);

    /**
     * Add lore line.
     *
     * @param line The line.
     * @return The builder.
     */
    ItemBuilder addLoreLine(@NotNull String line);

    /**
     * Add lore line.
     *
     * @param line The line.
     * @return The builder.
     */
    ItemBuilder addLoreLine(@NotNull Supplier<String> line);

    /**
     * Add lore lines.
     *
     * @param lines The lines.
     * @return The builder.
     */
    ItemBuilder addLoreLines(@NotNull List<String> lines);

    /**
     * Add lore lines.
     *
     * @param lines The lines.
     * @return The builder.
     */
    ItemBuilder addLoreLines(@NotNull Supplier<List<String>> lines);

    /**
     * Add ItemFlags.
     *
     * @param itemFlags The flags.
     * @return The builder.
     */
    ItemBuilder addItemFlag(@NotNull ItemFlag... itemFlags);

    /**
     * Add ItemFlags.
     *
     * @param itemFlags The flags.
     * @return The builder.
     */
    ItemBuilder addItemFlag(@NotNull Supplier<ItemFlag[]> itemFlags);

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
    <A, B> ItemBuilder writeMetaKey(@NotNull NamespacedKey key,
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
    <A, B> ItemBuilder writeMetaKey(@NotNull Supplier<NamespacedKey> key,
                                    @NotNull Supplier<PersistentDataType<A, B>> type,
                                    @NotNull Supplier<B> value);

    /**
     * Set unbreakable.
     *
     * @param unbreakable If the item should be unbreakable.
     * @return The builder.
     */
    ItemBuilder setUnbreakable(boolean unbreakable);

    /**
     * Set unbreakable.
     *
     * @param unbreakable If the item should be unbreakable.
     * @return The builder.
     */
    ItemBuilder setUnbreakable(@NotNull Supplier<Boolean> unbreakable);

    /**
     * Set custom model data.
     *
     * @param data The data.
     * @return The builder.
     */
    ItemBuilder setCustomModelData(@Nullable Integer data);

    /**
     * Set custom model data.
     *
     * @param data The data.
     * @return The builder.
     */
    ItemBuilder setCustomModelData(@NotNull Supplier<Integer> data);

    /**
     * Set data components on the item.
     * <p>
     * Components use the same format as item components in commands, keyed by
     * component id (for example <code>minecraft:attribute_modifiers</code>) and
     * valued with plain objects: maps, lists, strings, numbers, and booleans.
     * They are applied when the item is built.
     * <p>
     * Invalid components are skipped silently; use
     * {@link com.willfp.eco.core.items.Items#withComponents} to be told which
     * components failed.
     *
     * @param components The components.
     * @return The builder.
     */
    ItemBuilder setComponents(@NotNull Map<String, Object> components);

    /**
     * Set data components on the item.
     *
     * @param components The components, as a config section keyed by component id.
     * @return The builder.
     */
    ItemBuilder setComponents(@NotNull Config components);

    /**
     * Build the item.
     *
     * @return The item.
     */
    ItemStack build();
}
