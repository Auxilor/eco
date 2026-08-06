package com.willfp.eco.core.integrations.customentities;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper interface for custom entity integrations.
 * <p>
 * Implemented for plugins that add custom entities, such as MythicMobs, so that their
 * entities can be looked up through eco's own entity system.
 *
 * @see CustomEntitiesManager
 */
public interface CustomEntitiesIntegration extends Integration {
    /**
     * Register all of this plugin's custom entities into eco.
     *
     * @see com.willfp.eco.core.entities.Entities
     */
    void registerAllEntities();
}
