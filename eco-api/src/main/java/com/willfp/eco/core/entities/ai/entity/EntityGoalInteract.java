package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Interact with other mobs.
 *
 * @param targetClass The type of entity to interact with.
 * @param range       The range at which to interact.
 * @param chance      The chance for interaction, as a percentage.
 */
public record EntityGoalInteract(
        @NotNull Class<? extends LivingEntity> targetClass,
        double range,
        double chance
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalInteract> DESERIALIZER = new EntityGoalInteract.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    @SuppressWarnings("unchecked")
    private static final class Deserializer implements KeyedDeserializer<EntityGoalInteract> {
        @Override
        @Nullable
        public EntityGoalInteract deserialize(@NotNull final Config config) {
            if (!(
                    config.has("targetClass")
                            && config.has("range")
                            && config.has("chance")
            )) {
                return null;
            }

            try {
                return new EntityGoalInteract(
                        (Class<? extends LivingEntity>)
                                Objects.requireNonNull(
                                        EntityType.valueOf(config.getString("targetClass").toUpperCase()).getEntityClass()
                                ),
                        config.getDouble("range"),
                        config.getDouble("chance")
                );
            } catch (Exception e) {
                /*
                Exceptions could be caused by configs having values of a wrong type,
                invalid enum parameters, etc. Serializers shouldn't throw exceptions,
                so we encapsulate them as null.
                 */
                return null;
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("interact");
        }
    }
}
