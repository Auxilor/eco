package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * FastItemStack contains methods to modify and read items faster than in default bukkit.
 * <p>
 * If the ItemStack wrapped is a CraftItemStack, then the instance will be modified, allowing for set methods to work.
 * <p>
 * Otherwise, apply() must be called in order to apply the changes.
 * <p>
 * apply() <b>will</b> call getItemMeta and setItemMeta which will hurt performance, however this will still be faster.
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
     * Apply the changes made in FastItemStack.
     * <p>
     * If the ItemStack was a CraftItemStack, then no code will run - the changes are automatically applied.
     * <p>
     * If the ItemStack wasn't a CraftItemStack, then the unwrapped ItemStack's ItemMeta will be applied to the original ItemStack.
     * <p>
     * You should <b>always</b> call apply() if you have used any set methods.
     */
    void apply();

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
