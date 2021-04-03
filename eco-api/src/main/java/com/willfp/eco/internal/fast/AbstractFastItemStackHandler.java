package com.willfp.eco.internal.fast;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractFastItemStackHandler {
    Map<Enchantment, Integer> getEnchantments();

    int getEnchantmentLevel(@NotNull Enchantment enchantment);

    List<String> getLore();

    void setLore(@NotNull List<String> lore);

    Set<ItemFlag> getItemFlags();

    void setItemFlags(@NotNull Set<ItemFlag> flags);

    <T, Z> void writePersistentKey(@NotNull NamespacedKey key,
                                   @NotNull PersistentDataType<T, Z> type,
                                   @NotNull Z value);

    <T, Z> Z readPersistentKey(@NotNull NamespacedKey key,
                               @NotNull PersistentDataType<T, Z> type);

    Set<NamespacedKey> getPersistentKeys();

    String getDisplayName();

    void setDisplayName(@NotNull String name);

    void apply();
}
