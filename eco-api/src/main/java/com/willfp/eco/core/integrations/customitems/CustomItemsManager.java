package com.willfp.eco.core.integrations.customitems;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle custom item integrations.
 */
public final class CustomItemsManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<CustomItemsIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomItemsIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerAllItems() {
        REGISTRY.forEachSafely(CustomItemsIntegration::registerAllItems);
    }

    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerProviders() {
        REGISTRY.forEachSafely(CustomItemsIntegration::registerProvider);
    }

    private CustomItemsManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
