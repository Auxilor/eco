package com.willfp.eco.core.integrations.customitems;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper class for custom item integrations.
 */
public interface CustomItemsWrapper extends Integration {
    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    void registerAllItems();
}
