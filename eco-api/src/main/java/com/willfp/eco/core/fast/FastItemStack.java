package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FastItemStack contains methods to modify and read items faster than in default bukkit.
 * <p>
 * A FastItemStack wraps an existing {@link ItemStack} and operates on its underlying NMS handle,
 * avoiding the cost of creating and applying ItemMeta. Every mutating method writes back to the
 * wrapped ItemStack immediately, so there is no explicit "save" step; the wrapped stack is the
 * same instance returned by {@link #unwrap()}. The {@link PersistentDataContainer} returned by
 * {@link PersistentDataHolder#getPersistentDataContainer()} also writes back on every change.
 */
public interface FastItemStack extends PersistentDataHolder {
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
     * <p>
     * If stored enchantments are accounted for, an enchantment present both normally and stored
     * is reported at the higher of the two levels.
     *
     * @param checkStored If stored enchantments should be accounted for.
     * @return A map of all enchantments.
     */
    @NotNull
    Map<Enchantment, Integer> getEnchants(boolean checkStored);

    /**
     * Get the level of an enchantment.
     *
     * @param enchantment The enchantment.
     * @return The enchantment level, or 0 if not found.
     */
    default int getEnchantmentLevel(@NotNull Enchantment enchantment) {
        return getEnchantmentLevel(enchantment, false);
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
     * Set the item lore, as legacy strings.
     *
     * @param lore The lore, null to remove the lore.
     */
    void setLore(@Nullable List<String> lore);

    /**
     * Set the item lore, as components.
     *
     * @param lore The lore, null to remove the lore.
     */
    void setLoreComponents(@Nullable List<Component> lore);

    /**
     * Get the item lore, as legacy strings.
     *
     * @return The lore, empty if the item has no lore.
     */
    List<String> getLore();

    /**
     * Get the item lore, as components.
     *
     * @return The lore, empty if the item has no lore.
     */
    List<Component> getLoreComponents();

    /**
     * Set the item name.
     *
     * @param name The name, null to remove the name.
     */
    void setDisplayName(@Nullable Component name);

    /**
     * Set the item name.
     *
     * @param name The name, null to remove the name.
     */
    void setDisplayName(@Nullable String name);

    /**
     * Get the item display name, as a component.
     *
     * @return The display name, falling back to the item's default name if none is set.
     */
    Component getDisplayNameComponent();

    /**
     * Get the item display name, as a legacy string.
     *
     * @return The display name, falling back to the item's default name if none is set.
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
     * Add ItemFlags, hiding the corresponding parts of the tooltip.
     *
     * @param hideFlags The flags.
     */
    void addItemFlags(@NotNull ItemFlag... hideFlags);

    /**
     * Remove ItemFlags, showing the corresponding parts of the tooltip again.
     *
     * @param hideFlags The flags.
     */
    void removeItemFlags(@NotNull ItemFlag... hideFlags);

    /**
     * Get the ItemFlags currently set on the item.
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
     * @throws UnsupportedOperationException Always, on 1.20.5 and above.
     * @deprecated Items are now component-based.
     */
    @Deprecated(forRemoval = true, since = "6.70.0")
    default PersistentDataContainer getBaseTag() {
        throw new UnsupportedOperationException("Not supported in 1.20.5+");
    }

    /**
     * Set the base NBT tag (Not PublicBukkitValues, the base) from a PersistentDataContainer.
     *
     * @param container The PersistentDataContainer.
     * @throws UnsupportedOperationException Always, on 1.20.5 and above.
     * @deprecated Items are now component-based.
     */
    @Deprecated(forRemoval = true, since = "6.70.0")
    default void setBaseTag(@Nullable PersistentDataContainer container) {
        throw new UnsupportedOperationException("Not supported in 1.20.5+");
    }

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
     * <p>
     * Custom model data is no longer integer-based since 1.21.3, so this returns null there.
     *
     * @return The data, or null if none.
     */
    @Nullable
    Integer getCustomModelData();

    /**
     * Set the custom model data.
     * <p>
     * Custom model data is no longer integer-based since 1.21.3, so only removal (passing null)
     * has an effect there.
     *
     * @param data The data, null to remove.
     */
    void setCustomModelData(@Nullable Integer data);

    /**
     * Get the Bukkit ItemStack again.
     * <p>
     * This is the same instance that was wrapped, already carrying every change made through
     * this FastItemStack.
     *
     * @return The ItemStack.
     */
    @NotNull
    ItemStack unwrap();

    /**
     * Wrap an ItemStack to create a FastItemStack.
     * <p>
     * The wrapped ItemStack is modified in place by this FastItemStack.
     *
     * @param itemStack The ItemStack, null to wrap a new {@link Material#AIR} stack instead.
     * @return The FastItemStack.
     */
    static FastItemStack wrap(@Nullable final ItemStack itemStack) {
        return Eco.get().createFastItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR)));
    }
}
