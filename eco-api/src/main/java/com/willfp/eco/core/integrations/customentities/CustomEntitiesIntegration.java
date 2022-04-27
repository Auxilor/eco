package com.willfp.eco.core.integrations.customentities;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper class for custom item integrations.
 */
public interface CustomEntitiesIntegration extends Integration {
    /**
     * Register all the custom entities for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.entities.Entities
     */
    void registerAllEntities();
}
