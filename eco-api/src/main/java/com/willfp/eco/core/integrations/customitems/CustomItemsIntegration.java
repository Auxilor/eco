package com.willfp.eco.core.integrations.customitems;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper class for custom item integrations.
 */
public interface CustomItemsIntegration extends Integration {
    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    default void registerAllItems() {
        // Override when needed.
    }

    /**
     * Register {@link com.willfp.eco.core.items.provider.ItemProvider}s.
     */
    default void registerProvider() {
        // Override when needed.
    }
}
