package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
     * Get bukkit ItemStack again.
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
    static FastItemStack wrap(@NotNull final ItemStack itemStack) {
        return Eco.getHandler().createFastItemStack(itemStack);
    }
}
