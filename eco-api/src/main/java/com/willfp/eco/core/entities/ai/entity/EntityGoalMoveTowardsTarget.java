package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to move towards a target.
 *
 * @param speed       The speed at which to move towards the target.
 * @param maxDistance The maximum distance the target can be where the entity will still move towards it.
 */
public record EntityGoalMoveTowardsTarget(
        double speed,
        double maxDistance
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalMoveTowardsTarget> DESERIALIZER = new EntityGoalMoveTowardsTarget.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalMoveTowardsTarget> {
        @Override
        @Nullable
        public EntityGoalMoveTowardsTarget deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("maxDistance")
            )) {
                return null;
            }

            return new EntityGoalMoveTowardsTarget(
                    config.getDouble("speed"),
                    config.getDouble("maxDistance")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("move_towards_target");
        }
    }
}
