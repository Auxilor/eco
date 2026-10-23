package com.willfp.eco.core.integrations.model;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Class to handle model plugin integrations, such as ModelEngine or BetterModel.
 * <p>
 * Calls on an entity go to every integration that has a model on it, so a plugin never needs
 * to know which model plugin drew the entity.
 */
public final class ModelManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<ModelIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ModelIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Unregister an integration.
     *
     * @param integration The integration to unregister.
     */
    public static void unregister(@NotNull final ModelIntegration integration) {
        REGISTRY.remove(integration);
    }

    /**
     * Get all registered integrations.
     *
     * @return The integrations.
     */
    @NotNull
    public static Set<ModelIntegration> getRegisteredIntegrations() {
        return new HashSet<>(REGISTRY.values());
    }

    /**
     * Get an integration by its config ID or plugin name, ignoring case and separators, so
     * {@code model-engine}, {@code modelengine} and {@code ModelEngine} are all the same.
     *
     * @param id The ID or plugin name.
     * @return The integration, or null if none is registered with that ID.
     */
    @Nullable
    public static ModelIntegration getIntegration(@NotNull final String id) {
        String key = normalise(id);

        for (ModelIntegration integration : REGISTRY.values()) {
            if (normalise(integration.getId()).equals(key) || normalise(integration.getPluginName()).equals(key)) {
                return integration;
            }
        }

        return null;
    }

    /**
     * Add a model to an entity.
     *
     * @param entity        The entity.
     * @param integrationId The ID or plugin name of the model plugin.
     * @param modelId       The model ID.
     * @return If the entity now has the model.
     */
    public static boolean attachModel(@NotNull final Entity entity,
                                      @NotNull final String integrationId,
                                      @NotNull final String modelId) {
        ModelIntegration target = getIntegration(integrationId);

        return REGISTRY.anySafely(integration -> integration == target && integration.attachModel(entity, modelId));
    }

    /**
     * Get if an entity has a model.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for any model.
     * @return If the entity has the model.
     */
    public static boolean hasModel(@NotNull final Entity entity,
                                   @Nullable final String modelId) {
        return REGISTRY.anySafely(integration -> integration.hasModel(entity, modelId));
    }

    /**
     * Show or hide an entity under its models.
     *
     * @param entity  The entity.
     * @param visible If the entity should be visible.
     */
    public static void setBaseEntityVisible(@NotNull final Entity entity,
                                            final boolean visible) {
        forEachModelled(entity, null, integration -> {
            integration.setBaseEntityVisible(entity, visible);
            return true;
        });
    }

    /**
     * Play an animation on an entity's models.
     *
     * @param entity    The entity.
     * @param modelId   The model ID, or null for every model.
     * @param name      The animation name.
     * @param animation How to play the animation.
     * @return If the animation is playing on any model.
     */
    public static boolean playAnimation(@NotNull final Entity entity,
                                        @Nullable final String modelId,
                                        @NotNull final String name,
                                        @NotNull final ModelAnimation animation) {
        return forEachModelled(entity, modelId, integration -> integration.playAnimation(entity, modelId, name, animation));
    }

    /**
     * Stop an animation on an entity's models.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param name    The animation name.
     * @param fade    If the animation should blend out rather than stop straight away.
     * @return If the animation was stopped on any model.
     */
    public static boolean stopAnimation(@NotNull final Entity entity,
                                        @Nullable final String modelId,
                                        @NotNull final String name,
                                        final boolean fade) {
        return forEachModelled(entity, modelId, integration -> integration.stopAnimation(entity, modelId, name, fade));
    }

    /**
     * Get if an animation is playing on an entity's models.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param name    The animation name.
     * @return If the animation is playing on any model.
     */
    public static boolean isPlayingAnimation(@NotNull final Entity entity,
                                             @Nullable final String modelId,
                                             @NotNull final String name) {
        return REGISTRY.anySafely(integration -> integration.isPlayingAnimation(entity, modelId, name));
    }

    /**
     * Set the animation an entity's models play by themselves in a state.
     *
     * @param entity  The entity.
     * @param modelId The model ID, or null for every model.
     * @param state   The state.
     * @param name    The animation name.
     */
    public static void setStateAnimation(@NotNull final Entity entity,
                                         @Nullable final String modelId,
                                         @NotNull final ModelState state,
                                         @NotNull final String name) {
        forEachModelled(entity, modelId, integration -> {
            integration.setStateAnimation(entity, modelId, state, name);
            return true;
        });
    }

    private static boolean forEachModelled(@NotNull final Entity entity,
                                           @Nullable final String modelId,
                                           @NotNull final Predicate<ModelIntegration> action) {
        boolean[] any = {false};

        REGISTRY.forEachSafely(integration -> {
            if (integration.hasModel(entity, modelId) && action.test(integration)) {
                any[0] = true;
            }
        });

        return any[0];
    }

    private static String normalise(@NotNull final String id) {
        return id.toLowerCase().replace("-", "").replace("_", "");
    }

    private ModelManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
