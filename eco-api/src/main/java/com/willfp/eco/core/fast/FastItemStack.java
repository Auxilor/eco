package com.willfp.eco.core.fast;

import com.willfp.eco.core.Eco;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * FastItemStack contains methods to modify and read items faster than in default bukkit.
 */
@DelegateDeserialization(FastItemStackSerialization.class)
public interface FastItemStack extends PersistentDataHolder, ConfigurationSerializable {

    boolean hasEnchantmentsOnItem();

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

    boolean hasLore();

    /**
     * Set the item lore.
     *
     * @param lore The lore.
     */
    void setLore(@Nullable List<String> lore);

    default void setLore(String... lore) {
        this.setLore(Arrays.asList(lore));
    }

    /**
     * Get the item lore.
     *
     * @return The lore.
     */
    List<String> getLore();

    boolean hasItemFlags();

    void setItemFlags(Set<ItemFlag> itemFlags);

    default void setItemFlags(ItemFlag... itemFlags) {
        Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
        flags.addAll(Arrays.asList(itemFlags));
        this.setItemFlags(flags);
    }

    Set<ItemFlag> getItemFlags();

    boolean hasDisplayName();

    void setDisplayName(String displayName);

    String getDisplayName();

    void setUnbreakable(boolean unbreakable);

    boolean isUnbreakable();

    void setType(Material material);

    Material getType();

    boolean isStackable();

    default boolean isUnstackable() {
        return !this.isStackable();
    }

    void setAmount(int amount);

    int getAmount();

    default void shrink(int amount) {
        this.setAmount(this.getAmount()-amount);
    }

    default void shrink() {
        this.shrink(1);
    }

    int getDurability();

    void setDurability(int durability);

    boolean isSimilar(FastItemStack itemStack);

    default boolean isSimilar(ItemStack itemStack) {
        return this.isSimilar(FastItemStack.wrap(itemStack));
    }

    /**
     * Get the Bukkit ItemStack again.
     *
     * @return The ItemStack.
     */
    @Contract("-> new")
    @NotNull
    ItemStack unwrap();

    /**
     * Wrap an ItemStack to create a FastItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The FastItemStack.
     */
    @Contract("_ -> new")
    @NotNull
    static FastItemStack wrap(final ItemStack itemStack) {
        return Eco.getHandler().createFastItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR)));
    }

    @NotNull
    @Override
    PersistentDataContainer getPersistentDataContainer();

    @Contract("-> new")
    @NotNull
    @Override
    Map<String, Object> serialize();
}
