package com.willfp.eco.core.drops;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Internal interface for backend DropQueue implementations.
 */
public interface InternalDropQueue {
    /**
     * Add item to queue.
     *
     * @param item The item to add.
     * @return The DropQueue.
     */
    InternalDropQueue addItem(@NotNull ItemStack item);

    /**
     * Add multiple items to queue.
     *
     * @param itemStacks The items to add.
     * @return The DropQueue.
     */
    InternalDropQueue addItems(@NotNull Collection<ItemStack> itemStacks);

    /**
     * Add xp to queue.
     *
     * @param amount The amount to add.
     * @return The DropQueue.
     */
    InternalDropQueue addXP(int amount);

    /**
     * Set location of the origin of the drops.
     *
     * @param location The location.
     * @return The DropQueue.
     */
    InternalDropQueue setLocation(@NotNull Location location);

    /**
     * Force the queue to act as if player is telekinetic.
     *
     * @return The DropQueue.
     */
    InternalDropQueue forceTelekinesis();

    /**
     * Push the queue.
     */
    void push();
}
