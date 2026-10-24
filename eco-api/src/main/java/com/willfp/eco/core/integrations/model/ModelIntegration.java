package com.willfp.eco.core.integrations.model;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for model plugin integrations, such as ModelEngine or BetterModel.
 * <p>
 * Every method that takes a model ID acts on every model on the entity when the ID is null.
 */
public interface ModelIntegration extends Integration {
    /**
     * Get the ID used for this integration in configs, such as {@code model-engine}.
     *
     * @return The ID.
     */
    @NotNull
    String getId();

    /**
     * Get if a model exists.
     *
     * @param modelId The model ID.
     * @return If the model exists.
     */
    boolean modelExists(@NotNull String modelId);

    /**
     * Add a model to an entity.
     *
     * @param entity  The entity.
     * @param modelId The model ID.
     * @return If the entity now has the model.
     */
    boolean attachModel(@NotNull Entity entity,
                        @NotNull String modelId);

    /**
     * Get if an entity has a model.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for any model.
     * @return If the entity has the model.
     */
    boolean hasModel(@NotNull Entity entity,
                     @Nullable String modelId);

    /**
     * Show or hide the entity under its models.
     *
     * @param entity  The entity.
     * @param visible If the entity should be visible.
     */
    default void setBaseEntityVisible(@NotNull final Entity entity,
                                      final boolean visible) {
    }

    /**
     * Play an animation.
     *
     * @param entity    The entity.
     * @param modelId   The model ID, or null for every model.
     * @param name      The animation name.
     * @param animation How to play the animation.
     * @return If the animation is playing on any model.
     */
    boolean playAnimation(@NotNull Entity entity,
                          @Nullable String modelId,
                          @NotNull String name,
                          @NotNull ModelAnimation animation);

    /**
     * Stop an animation.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param name    The animation name.
     * @param fade    If the animation should blend out rather than stop straight away.
     * @return If the animation was stopped on any model.
     */
    boolean stopAnimation(@NotNull Entity entity,
                          @Nullable String modelId,
                          @NotNull String name,
                          boolean fade);

    /**
     * Get if an animation is playing.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param name    The animation name.
     * @return If the animation is playing on any model.
     */
    boolean isPlayingAnimation(@NotNull Entity entity,
                               @Nullable String modelId,
                               @NotNull String name);

    /**
     * Set the animation a model plays by itself in a state, in place of the one the model
     * gives that state.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param state   The state.
     * @param name    The animation name.
     */
    default void setStateAnimation(@NotNull final Entity entity,
                                   @Nullable final String modelId,
                                   @NotNull final ModelState state,
                                   @NotNull final String name) {
    }
}
