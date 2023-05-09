package com.willfp.eco.core.integrations.customentities;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle custom entity integrations.
 */
public final class CustomEntitiesManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<CustomEntitiesIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomEntitiesIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Register all the custom entities for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.entities.Entities
     */
    public static void registerAllEntities() {
        REGISTRY.forEachSafely(CustomEntitiesIntegration::registerAllEntities);
    }

    private CustomEntitiesManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
