package com.willfp.eco.core.display;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for display implementations.
 */
public interface DisplayHandler {
    /**
     * Register display module.
     *
     * @param module The module.
     */
    void registerDisplayModule(@NotNull DisplayModule module);

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The ItemStack.
     */
    ItemStack display(@NotNull ItemStack itemStack,
                      @Nullable Player player);

    /**
     * Revert on ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    ItemStack revert(@NotNull ItemStack itemStack);

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    ItemStack finalize(@NotNull ItemStack itemStack);

    /**
     * Unfinalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    ItemStack unfinalize(@NotNull ItemStack itemStack);

    /**
     * If an item is finalized.
     *
     * @param itemStack The item.
     * @return If finalized.
     */
    boolean isFinalized(@NotNull ItemStack itemStack);
}
