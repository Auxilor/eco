package com.willfp.eco.core.integrations.customitems;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle custom item integrations.
 */
public final class CustomItemsManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<CustomItemsIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomItemsIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerAllItems() {
        for (CustomItemsIntegration customItemsIntegration : REGISTERED) {
            customItemsIntegration.registerAllItems();
        }
    }

    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerProviders() {
        for (CustomItemsIntegration customItemsIntegration : REGISTERED) {
            customItemsIntegration.registerProvider();
        }
    }

    private CustomItemsManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
