package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Avoid entities.
 *
 * @param entity    The entity type to avoid.
 * @param distance  The distance to flee to.
 * @param slowSpeed The slow movement speed.
 * @param fastSpeed The fast movement speed.
 */
public record EntityGoalAvoidEntity(
        @NotNull TestableEntity entity,
        double distance,
        double slowSpeed,
        double fastSpeed
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalAvoidEntity> DESERIALIZER = new Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalAvoidEntity> {
        @Override
        @Nullable
        public EntityGoalAvoidEntity deserialize(@NotNull final Config config) {
            if (!(
                    config.has("entity")
                            && config.has("distance")
                            && config.has("slowSpeed")
                            && config.has("fastSpeed")
            )) {
                return null;
            }

            TestableEntity entity = Entities.lookup(config.getString("entity"));

            return new EntityGoalAvoidEntity(
                    entity,
                    config.getDouble("distance"),
                    config.getDouble("slowSpeed"),
                    config.getDouble("fastSpeed")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("avoid_entity");
        }
    }
}
