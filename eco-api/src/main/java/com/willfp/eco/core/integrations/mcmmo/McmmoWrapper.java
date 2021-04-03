package com.willfp.eco.core.integrations.mcmmo;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface McmmoWrapper {
    /**
     * Get bonus drop count of block.
     *
     * @param block The block.
     * @return The drop multiplier.
     */
    int getBonusDropCount(@NotNull Block block);

    /**
     * Get if event is fake.
     *
     * @param event The event.
     * @return If is fake.
     */
    boolean isFake(@NotNull Event event);
}
