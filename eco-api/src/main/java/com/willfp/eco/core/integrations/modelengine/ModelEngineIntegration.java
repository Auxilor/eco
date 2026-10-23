package com.willfp.eco.core.integrations.modelengine;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wrapper interface for ModelEngine integrations.
 *
 * @see ModelEngineManager
 */
public interface ModelEngineIntegration extends Integration {
    /**
     * Create a model from a blueprint.
     *
     * @param id The blueprint ID.
     * @return The model, or null if no blueprint has that ID.
     */
    @Nullable
    BridgedActiveModel createActiveModel(@NotNull String id);

    /**
     * Create a modeled entity, or get the existing one.
     *
     * @param entity The base entity.
     * @return The modeled entity.
     */
    @NotNull
    BridgedModeledEntity createModeledEntity(@NotNull Entity entity);

    /**
     * Get a modeled entity.
     *
     * @param uuid The UUID of the base entity.
     * @return The modeled entity, or null if the entity has no models.
     */
    @Nullable
    BridgedModeledEntity getModeledEntity(@NotNull UUID uuid);
}
