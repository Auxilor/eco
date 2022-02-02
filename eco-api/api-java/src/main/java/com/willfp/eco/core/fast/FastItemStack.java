package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
     * Set the rework penalty.
     *
     * @param cost The rework penalty to set.
     */
    void setRepairCost(int cost);

    /**
     * Get the rework penalty.
     * .
     *
     * @return The rework penalty found on the item.
     */
    int getRepairCost();

    /**
     * Add ItemFlags.
     *
     * @param hideFlags The flags.
     */
    void addItemFlags(@NotNull ItemFlag... hideFlags);

    /**
     * Remove ItemFlags.
     *
     * @param hideFlags The flags.
     */
    void removeItemFlags(@NotNull ItemFlag... hideFlags);

    /**
     * Get the ItemFlags.
     *
     * @return The flags.
     */
    Set<ItemFlag> getItemFlags();

    /**
     * Test the item for a flag.
     *
     * @param flag The flag.
     * @return If the flag is present.
     */
    boolean hasItemFlag(@NotNull ItemFlag flag);

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
    static FastItemStack wrap(@Nullable final ItemStack itemStack) {
        return Eco.getHandler().createFastItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR)));
    }
}
