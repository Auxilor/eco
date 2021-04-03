package com.willfp.eco.core.fast;

import com.willfp.eco.internal.fast.AbstractFastItemStackHandler;
import com.willfp.eco.internal.fast.FastItemStackHandlerFactory;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FastItemStack {
    /**
     * The ItemStack handler.
     */
    private static FastItemStackHandlerFactory factory;

    /**
     * The ItemStack to interface with.
     */
    private final AbstractFastItemStackHandler handle;

    /**
     * Create a new fast ItemStack.
     *
     * @param itemStack The ItemStack.
     */
    public FastItemStack(@NotNull final ItemStack itemStack) {
        this.handle = factory.create(itemStack);
    }

    /**
     * Get the enchantments on the item.
     *
     * @return The enchantments.
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return handle.getEnchantments();
    }

    /**
     * Get the level of a specific enchantment.
     *
     * @param enchantment The enchantments.
     * @return The level, or 0 if not found.
     */
    public int getEnchantmentLevel(@NotNull final Enchantment enchantment) {
        return handle.getEnchantmentLevel(enchantment);
    }

    /**
     * Get the lore of an item.
     *
     * @return The lore.
     */
    public List<String> getLore() {
        return handle.getLore();
    }

    /**
     * Set the lore of an item.
     *
     * @param lore The lore.
     */
    public void setLore(@NotNull final List<String> lore) {
        handle.setLore(lore);
    }

    /**
     * Get the item flags on an item.
     *
     * @return The item flags.
     */
    public Set<ItemFlag> getItemFlags() {
        return handle.getItemFlags();
    }

    /**
     * Set the item flags on an item.
     *
     * @param flags The flags.
     */
    public void setItemFlags(@NotNull final Set<ItemFlag> flags) {
        handle.setItemFlags(flags);
    }

    /**
     * Write a key in persistent meta.
     *
     * @param key   The key.
     * @param type  The type.
     * @param value The value.
     * @param <T>   The type.
     * @param <Z>   The type.
     */
    public <T, Z> void writePersistentKey(@NotNull final NamespacedKey key,
                                          @NotNull final PersistentDataType<T, Z> type,
                                          @NotNull final Z value) {
        handle.writePersistentKey(key, type, value);
    }

    /**
     * Read a key from persistent meta.
     *
     * @param key  The key.
     * @param type The type.
     * @param <T>  The type.
     * @param <Z>  The type.
     * @return The value.
     */
    public <T, Z> Z readPersistentKey(@NotNull final NamespacedKey key,
                                      @NotNull final PersistentDataType<T, Z> type) {
        return handle.readPersistentKey(key, type);
    }

    /**
     * Get persistent meta keys.
     *
     * @return The keys.
     */
    public Set<NamespacedKey> getPersistentKeys() {
        return handle.getPersistentKeys();
    }

    /**
     * Get the display name
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return handle.getDisplayName();
    }

    /**
     * Set the display name.
     *
     * @param name The name.
     */
    public void setDisplayName(@NotNull final String name) {
        handle.setDisplayName(name);
    }

    /**
     * Apply changes.
     */
    public void apply() {
        handle.apply();
    }

    /**
     * Initialize the ItemStack handler factory.
     *
     * @param handlerFactory The handler factory.
     */
    @ApiStatus.Internal
    public static void initialize(@NotNull final FastItemStackHandlerFactory handlerFactory) {
        factory = handlerFactory;
    }
}
