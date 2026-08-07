package com.willfp.eco.core.integrations.mcmmo;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper interface for mcMMO integrations.
 * <p>
 * Lets eco account for mcMMO's bonus block drops, and ignore the fake events that mcMMO fires
 * internally (for example when simulating block breaks for abilities).
 *
 * @see McmmoManager
 */
public interface McmmoIntegration extends Integration {
    /**
     * Get the number of extra drops mcMMO grants for a block.
     *
     * @param block The block.
     * @return The bonus drop count.
     */
    int getBonusDropCount(@NotNull Block block);

    /**
     * Get if event is fake.
     *
     * @param event The event.
     * @return If the event is a fake event fired by mcMMO.
     */
    boolean isFake(@NotNull Event event);
}
