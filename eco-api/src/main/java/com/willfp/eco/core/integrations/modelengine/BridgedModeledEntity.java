package com.willfp.eco.core.integrations.modelengine;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An entity with ModelEngine models attached.
 */
public interface BridgedModeledEntity {
    /**
     * Get if the base entity is visible.
     *
     * @return If visible.
     */
    boolean isBaseEntityVisible();

    /**
     * Set if the base entity is visible.
     *
     * @param visible If visible.
     */
    void setBaseEntityVisible(boolean visible);

    /**
     * Get the models attached to the entity.
     *
     * @return The models, keyed by ID.
     */
    @NotNull
    Map<String, BridgedActiveModel> getModels();

    /**
     * Attach a model to the entity.
     *
     * @param model The model.
     */
    void addModel(@NotNull BridgedActiveModel model);

    /**
     * Remove a model from the entity.
     *
     * @param model The model.
     */
    void removeModel(@NotNull BridgedActiveModel model);
}
