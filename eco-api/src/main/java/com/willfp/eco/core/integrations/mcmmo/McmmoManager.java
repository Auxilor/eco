package com.willfp.eco.core.integrations.mcmmo;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class McmmoManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<McmmoWrapper> regsistered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final McmmoWrapper integration) {
        regsistered.add(integration);
    }

    /**
     * Get bonus drop count from block.
     *
     * @param block The block.
     * @return The bonus drop count.
     */
    public int getBonusDropCount(@NotNull final Block block) {
        for (McmmoWrapper mcmmoWrapper : regsistered) {
            return mcmmoWrapper.getBonusDropCount(block);
        }
        return 0;
    }

    /**
     * Get if event is fake.
     *
     * @param event The event to check.
     * @return If the event is fake.
     */
    public boolean isFake(@NotNull final Event event) {
        for (McmmoWrapper mcmmoWrapper : regsistered) {
            return mcmmoWrapper.isFake(event);
        }
        return false;
    }
}
