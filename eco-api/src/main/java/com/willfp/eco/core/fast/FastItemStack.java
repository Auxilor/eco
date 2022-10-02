package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * FastItemStack contains methods to modify and read items faster than in default bukkit.
 */
public interface FastItemStack extends PersistentDataHolder {
    /**
     * Get all enchantments on an item.
     *
     * @param checkStored If stored NBT should also be checked.
     * @return A map of all enchantments.
     * @deprecated Poorly named method. Use getEnchants instead.
     */
    @Deprecated(since = "6.24.0", forRemoval = true)
    default Map<Enchantment, Integer> getEnchantmentsOnItem(boolean checkStored) {
        return getEnchants(checkStored);
    }

    /**
     * Get all enchantments on an item.
     * Does not account for stored enchants.
     *
     * @return A map of all enchantments.
     */
    default Map<Enchantment, Integer> getEnchants() {
        return getEnchants(false);
    }

    /**
     * Get all enchantments on an item.
     *
     * @param checkStored If stored enchantments should be accounted for.
     * @return A map of all enchantments.
     */
    @NotNull
    Map<Enchantment, Integer> getEnchants(boolean checkStored);

    /**
     * Get the level of an enchantment on an item.
     *
     * @param enchantment The enchantment.
     * @return The enchantment level, or 0 if not found.
     * @deprecated Poorly named method. Use getEnchantmentLevel instead.
     */
    @Deprecated(since = "6.34.0", forRemoval = true)
    default int getLevelOnItem(@NotNull Enchantment enchantment) {
        return getEnchantmentLevel(enchantment, false);
    }

    /**
     * Get the level of an enchantment on an item.
     *
     * @param enchantment The enchantment.
     * @param checkStored If the stored NBT should also be checked.
     * @return The enchantment level, or 0 if not found.
     * @deprecated Poorly named method. Use getEnchantmentLevel instead.
     */
    @Deprecated(since = "6.34.0", forRemoval = true)
    @SuppressWarnings("DeprecatedIsStillUsed")
    default int getLevelOnItem(@NotNull Enchantment enchantment,
                               boolean checkStored) {
        return getEnchantmentLevel(enchantment, checkStored);
    }

    /**
     * Get the level of an enchantment.
     *
     * @param enchantment The enchantment.
     * @return The enchantment level, or 0 if not found.
     */
    default int getEnchantmentLevel(@NotNull Enchantment enchantment) {
        return getLevelOnItem(enchantment, false);
    }

    /**
     * Get the level of an enchantment.
     *
     * @param enchantment The enchantment.
     * @param checkStored If the stored NBT should also be checked.
     * @return The enchantment level, or 0 if not found.
     */
    int getEnchantmentLevel(@NotNull Enchantment enchantment,
                            boolean checkStored);

    /**
     * Set the item lore.
     *
     * @param lore The lore.
     */
    void setLore(@Nullable List<String> lore);

    /**
     * Set the item lore.
     *
     * @param lore The lore.
     */
    void setLoreComponents(@Nullable List<Component> lore);

    /**
     * Get the item lore.
     *
     * @return The lore.
     */
    List<String> getLore();

    /**
     * Get the item lore.
     *
     * @return The lore.
     */
    List<Component> getLoreComponents();

    /**
     * Set the item name.
     *
     * @param name The name.
     */
    void setDisplayName(@Nullable Component name);

    /**
     * Set the item name.
     *
     * @param name The name.
     */
    void setDisplayName(@Nullable String name);

    /**
     * Get the item display name.
     *
     * @return The display name.
     */
    Component getDisplayNameComponent();

    /**
     * Get the item display name.
     *
     * @return The display name.
     */
    String getDisplayName();

    /**
     * Set the rework penalty.
     *
     * @param cost The rework penalty to set.
     */
    void setRepairCost(int cost);

    /**
     * Get the rework penalty.
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
     * Get the base NBT tag (Not PublicBukkitValues, the base) as a PersistentDataContainer.
     * <p>
     * The returned PersistentDataContainer will not modify the item until the tag is set.
     *
     * @return The base NBT tag.
     */
    PersistentDataContainer getBaseTag();

    /**
     * Set the base NBT tag (Not PublicBukkitValues, the base) from a PersistentDataContainer.
     *
     * @param container The PersistentDataContainer.
     */
    void setBaseTag(@Nullable PersistentDataContainer container);

    /**
     * Get the type of the item.
     *
     * @return The type.
     */
    @NotNull
    Material getType();

    /**
     * Set the type of the item.
     *
     * @param material The type.
     */
    void setType(@NotNull Material material);

    /**
     * Get the amount of the item.
     *
     * @return The amount.
     */
    int getAmount();

    /**
     * Set the amount of the item.
     *
     * @param amount The amount.
     */
    void setAmount(int amount);

    /**
     * Get the custom model data.
     *
     * @return The data, or null if none.
     */
    @Nullable
    Integer getCustomModelData();

    /**
     * Set the custom model data.
     *
     * @param data The data, null to remove.
     */
    void setCustomModelData(@Nullable Integer data);

    /**
     * Get the Bukkit ItemStack again.
     *
     * @return The ItemStack.
     */
    @NotNull
    ItemStack unwrap();

    /**
     * Wrap an ItemStack to create a FastItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The FastItemStack.
     */
    static FastItemStack wrap(@Nullable final ItemStack itemStack) {
        return Eco.get().createFastItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR)));
    }
}
