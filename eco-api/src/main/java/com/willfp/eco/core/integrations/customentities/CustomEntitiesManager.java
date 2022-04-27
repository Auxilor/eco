package com.willfp.eco.core.integrations.customentities;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle custom entity integrations.
 */
public final class CustomEntitiesManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<CustomEntitiesIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomEntitiesIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Register all the custom entities for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.entities.Entities
     */
    public static void registerAllEntities() {
        for (CustomEntitiesIntegration integration : REGISTERED) {
            integration.registerAllEntities();
        }
    }

    private CustomEntitiesManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
