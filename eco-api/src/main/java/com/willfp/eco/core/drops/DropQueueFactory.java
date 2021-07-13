package com.willfp.eco.core.drops;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DropQueueFactory {
    /**
     * Create a DropQueue.
     * @param player The player.
     * @return The Queue.
     */
    DropQueue create(@NotNull Player player);
}
