package com.willfp.eco.core.drops;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Internal component to create backend DropQueue implementations.
 */
public interface DropQueueFactory {
    /**
     * Create a DropQueue.
     *
     * @param player The player.
     * @return The Queue.
     */
    InternalDropQueue create(@NotNull Player player);
}
