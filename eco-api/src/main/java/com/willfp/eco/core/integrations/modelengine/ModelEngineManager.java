package com.willfp.eco.core.integrations.modelengine;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Class to handle ModelEngine integrations.
 * <p>
 * If no {@link ModelEngineIntegration} is registered, no models can be created and no entity is
 * modeled.
 */
public final class ModelEngineManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<ModelEngineIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ModelEngineIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Get if a ModelEngine integration is registered.
     *
     * @return If ModelEngine is available.
     */
    public static boolean isEnabled() {
        return !REGISTRY.isEmpty();
    }

    /**
     * Create a model from a blueprint.
     *
     * @param id The blueprint ID.
     * @return The model, or null if no blueprint has that ID or ModelEngine is not available.
     */
    @Nullable
    public static BridgedActiveModel createActiveModel(@NotNull final String id) {
        return firstOrNull(integration -> integration.createActiveModel(id));
    }

    /**
     * Create a modeled entity, or get the existing one.
     *
     * @param entity The base entity.
     * @return The modeled entity, or null if ModelEngine is not available.
     */
    @Nullable
    public static BridgedModeledEntity createModeledEntity(@NotNull final Entity entity) {
        return firstOrNull(integration -> integration.createModeledEntity(entity));
    }

    /**
     * Get a modeled entity.
     *
     * @param entity The base entity.
     * @return The modeled entity, or null if the entity has no models.
     */
    @Nullable
    public static BridgedModeledEntity getModeledEntity(@NotNull final Entity entity) {
        return getModeledEntity(entity.getUniqueId());
    }

    /**
     * Get a modeled entity.
     *
     * @param uuid The UUID of the base entity.
     * @return The modeled entity, or null if the entity has no models.
     */
    @Nullable
    public static BridgedModeledEntity getModeledEntity(@NotNull final UUID uuid) {
        return firstOrNull(integration -> integration.getModeledEntity(uuid));
    }

    @Nullable
    private static <R> R firstOrNull(@NotNull final Function<ModelEngineIntegration, R> function) {
        return REGISTRY.firstSafely(integration -> Optional.ofNullable(function.apply(integration)), Optional.<R>empty())
                .orElse(null);
    }

    private ModelEngineManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
