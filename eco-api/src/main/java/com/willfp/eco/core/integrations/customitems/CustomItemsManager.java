package com.willfp.eco.core.integrations.customitems;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import java.util.HashSet;
import java.util.Set;
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
     * Register the custom items of every registered integration into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerAllItems() {
        REGISTRY.forEachSafely(CustomItemsIntegration::registerAllItems);
    }

    /**
     * Register the {@link com.willfp.eco.core.items.provider.ItemProvider}s of every
     * registered integration into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public static void registerProviders() {
        REGISTRY.forEachSafely(CustomItemsIntegration::registerProvider);
    }

    /**
     * Get all registered custom item integrations.
     *
     * @return The integrations.
     */
    public static Set<CustomItemsIntegration> getRegisteredIntegrations() {
        return new HashSet<>(REGISTRY.values());
    }

    private CustomItemsManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
