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
    private static final Set<CustomEntitiesWrapper> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomEntitiesWrapper integration) {
        REGISTERED.add(integration);
    }

    /**
     * Register all the custom entities for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.entities.Entities
     */
    public static void registerAllEntities() {
        for (CustomEntitiesWrapper wrapper : REGISTERED) {
            wrapper.registerAllEntities();
        }
    }

    private CustomEntitiesManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
