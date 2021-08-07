package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * FastItemStack contains methods to modify and read items faster than in default bukkit.
 */
public interface FastItemStack {
    /**
     * Get all enchantments on an item.
     *
     * @param checkStored If stored NBT should also be checked.
     * @return A map of all enchantments.
     */
    Map<Enchantment, Integer> getEnchantmentsOnItem(boolean checkStored);

    /**
     * Get the level of an enchantment on an item.
     *
     * @param enchantment The enchantment.
     * @param checkStored If the stored NBT should also be checked.
     * @return The enchantment level, or 0 if not found.
     */
    int getLevelOnItem(@NotNull Enchantment enchantment,
                       boolean checkStored);

    /**
     * Set the item lore.
     *
     * @param lore The lore.
     */
    void setLore(@Nullable List<String> lore);

    /**
     * Get the item lore.
     *
     * @return The lore.
     */
    List<String> getLore();

    /**
     * Get the Bukkit ItemStack again.
     *
     * @return The ItemStack.
     */
    ItemStack unwrap();

    /**
     * Wrap an ItemStack to create a FastItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The FastItemStack.
     */
    static FastItemStack wrap(final ItemStack itemStack) {
        return Eco.getHandler().createFastItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR)));
    }
}
