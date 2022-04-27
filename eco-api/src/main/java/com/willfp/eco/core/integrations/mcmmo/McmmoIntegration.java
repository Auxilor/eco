package com.willfp.eco.core.integrations.mcmmo;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for mcmmo integrations.
 */
public interface McmmoIntegration extends Integration {
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
