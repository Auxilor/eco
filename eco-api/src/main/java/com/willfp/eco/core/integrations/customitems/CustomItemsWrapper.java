package com.willfp.eco.core.integrations.customitems;

/**
 * Wrapper class for custom item integrations.
 */
public interface CustomItemsWrapper {
    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    void registerAllItems();
}
