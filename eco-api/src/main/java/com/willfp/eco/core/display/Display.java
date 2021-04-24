package com.willfp.eco.core.display;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class Display {
    /**
     * The prefix for lore lines.
     */
    public static final String PREFIX = "§z";

    /**
     * All registered display modules.
     */
    private static final Map<DisplayPriority, List<DisplayModule>> MODULES = new HashMap<>();

    /**
     * NamespacedKey for finalizing.
     */
    private static NamespacedKey finalizeKey = null;

    /**
     * Register display module.
     *
     * @param module The module.
     */
    public void registerDisplayModule(@NotNull final DisplayModule module) {
        List<DisplayModule> modules = MODULES.get(module.getPriority());

        modules.removeIf(module1 -> module1.getPluginName().equalsIgnoreCase(module.getPluginName()));

        modules.add(module);

        MODULES.put(module.getPriority(), modules);
    }

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack display(@NotNull final ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return itemStack; // return early if there's no customization of the item
        }

        Map<String, Object[]> pluginVarArgs = new HashMap<>();

        for (DisplayPriority priority : DisplayPriority.values()) {
            List<DisplayModule> modules = MODULES.get(priority);
            for (DisplayModule module : modules) {
                pluginVarArgs.put(module.getPluginName(), module.generateVarArgs(itemStack));
            }
        }

        revert(itemStack);

        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return itemStack;
        }

        for (DisplayPriority priority : DisplayPriority.values()) {
            List<DisplayModule> modules = MODULES.get(priority);
            for (DisplayModule module : modules) {
                Object[] varargs = pluginVarArgs.get(module.getPluginName());
                module.display(itemStack, varargs);
            }
        }

        return itemStack;
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack displayAndFinalize(@NotNull final ItemStack itemStack) {
        return finalize(display(itemStack));
    }

    /**
     * Revert on ItemStacks.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack revert(@NotNull final ItemStack itemStack) {
        if (Display.isFinalized(itemStack)) {
            unfinalize(itemStack);
        }

        if (!itemStack.hasItemMeta()) {
            return itemStack;
        }

        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return itemStack;
        }

        List<String> lore = meta.getLore();

        if (lore != null && lore.removeIf(line -> line.startsWith(Display.PREFIX))) { // only apply lore modification if needed
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }

        for (DisplayPriority priority : DisplayPriority.values()) {
            List<DisplayModule> modules = MODULES.get(priority);
            for (DisplayModule module : modules) {
                module.revert(itemStack);
            }
        }

        return itemStack;
    }

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack finalize(@NotNull final ItemStack itemStack) {
        Validate.notNull(finalizeKey, "Key cannot be null!");

        if (itemStack.getType().getMaxStackSize() > 1) {
            return itemStack;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(finalizeKey, PersistentDataType.INTEGER, 1);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Unfinalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack unfinalize(@NotNull final ItemStack itemStack) {
        Validate.notNull(finalizeKey, "Key cannot be null!");

        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return itemStack;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.remove(finalizeKey);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * If an item is finalized.
     *
     * @param itemStack The item.
     * @return If finalized.
     */
    public boolean isFinalized(@NotNull final ItemStack itemStack) {
        Validate.notNull(finalizeKey, "Key cannot be null!");

        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(finalizeKey, PersistentDataType.INTEGER);
    }

    /**
     * Set key to be used for finalization.
     *
     * @param finalizeKey The key.
     */
    @ApiStatus.Internal
    public static void setFinalizeKey(@NotNull final NamespacedKey finalizeKey) {
        Display.finalizeKey = finalizeKey;
    }

    static {
        for (DisplayPriority priority : DisplayPriority.values()) {
            MODULES.put(priority, new ArrayList<>());
        }
    }
}
