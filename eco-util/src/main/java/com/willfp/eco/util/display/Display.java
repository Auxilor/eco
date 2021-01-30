package com.willfp.eco.util.display;

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
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class Display {
    /**
     * Registered display functions.
     */
    private static final List<Map<String, Function<ItemStack, ItemStack>>> DISPLAY_FUNCTIONS = new ArrayList<>(10000);

    /**
     * Registered revert functions.
     */
    private static final List<Function<ItemStack, ItemStack>> REVERT_FUNCTIONS = new ArrayList<>();

    /**
     * NamespacedKey for finalizing.
     */
    private static NamespacedKey finalizeKey;

    /**
     * Register display module.
     *
     * @param module The module.
     */
    public void registerDisplayModule(@NotNull final DisplayModule module) {
        int priority = module.getPriority();
        if (priority > 9999) {
            priority = 9999;
        }
        Function<ItemStack, ItemStack> function = module.getFunction();

        Map<String, Function<ItemStack, ItemStack>> functions = DISPLAY_FUNCTIONS.get(priority);
        if (functions == null) {
            functions = new HashMap<>();
        }

        functions.remove(module.getId());
        functions.put(module.getId(), function);

        DISPLAY_FUNCTIONS.set(priority, functions);
    }

    /**
     * Register revert function.
     *
     * @param function The function.
     */
    public void registerRevertModule(@NotNull final Function<ItemStack, ItemStack> function) {
        REVERT_FUNCTIONS.add(function);
    }

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @return The itemstack.
     */
    public ItemStack display(@NotNull final ItemStack itemStack) {
        if (isFinalized(itemStack)) {
            unfinalize(itemStack);
            return itemStack;
        }

        revert(itemStack);

        for (Map<String, Function<ItemStack, ItemStack>> displayFunctions : DISPLAY_FUNCTIONS) {
            if (displayFunctions == null) {
                continue;
            }

            for (Function<ItemStack, ItemStack> displayFunction : displayFunctions.values()) {
                displayFunction.apply(itemStack);
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
        if (isFinalized(itemStack)) {
            unfinalize(itemStack);
            return itemStack;
        }

        for (Function<ItemStack, ItemStack> displayFunction : REVERT_FUNCTIONS) {
            displayFunction.apply(itemStack);
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
     * Register finalize function.
     *
     * @param function The function.
     * @deprecated Not needed.
     */
    @Deprecated
    public void registerFinalizeModule(@NotNull final Function<ItemStack, ItemStack> function) {
        // This function is not needed.
    }

    /**
     * Register finalize test function.
     *
     * @param function The function.
     * @deprecated Not needed.
     */
    @Deprecated
    public void registerFinalizeTestModule(@NotNull final Predicate<ItemStack> function) {
        // This isn't needed.
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
        for (int i = 0; i < 10000; i++) {
            DISPLAY_FUNCTIONS.add(null);
        }
    }
}
