package com.willfp.eco.core.drops;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Internal component to create backend DropQueue implementations.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
public interface DropQueueFactory {
    /**
     * Create a DropQueue.
     *
     * @param player The player.
     * @return The Queue.
     */
    InternalDropQueue create(@NotNull Player player);
}
