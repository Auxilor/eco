package com.willfp.eco.core.integrations.customitems;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper interface for custom item integrations.
 * <p>
 * Implemented for plugins that add custom items, such as ItemsAdder, Oraxen, Nexo,
 * CraftEngine, MythicMobs, ExecutableItems, HeadDatabase, and ItemBridge, so that their
 * items can be looked up through eco's own item system.
 *
 * @see CustomItemsManager
 */
public interface CustomItemsIntegration extends Integration {
    /**
     * Register all of this plugin's custom items into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    default void registerAllItems() {
        // Override when needed.
    }

    /**
     * Register this plugin's {@link com.willfp.eco.core.items.provider.ItemProvider}s into eco.
     */
    default void registerProvider() {
        // Override when needed.
    }
}
