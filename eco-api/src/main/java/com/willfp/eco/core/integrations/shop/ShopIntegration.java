package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for shop integrations.
 */
public interface ShopIntegration extends Integration {
    /**
     * Register eco item provider for shop plugins.
     */
    default void registerEcoProvider() {
        // Do nothing unless overridden.
    }

    /**
     * Get the sell event adapter.
     *
     * @return The listener.
     */
    @Nullable
    default Listener getSellEventAdapter() {
        // Do nothing unless overridden.
        return null;
    }
}
