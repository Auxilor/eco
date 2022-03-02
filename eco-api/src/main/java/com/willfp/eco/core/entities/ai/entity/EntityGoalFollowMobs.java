package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to follow and gather around all types of mobs, both hostile and neutral mobs.
 *
 * @param speed       The speed at which to follow.
 * @param minDistance The minimum follow distance.
 * @param maxDistance The maximum follow distance.
 */
public record EntityGoalFollowMobs(
        double speed,
        double minDistance,
        double maxDistance
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalFollowMobs> DESERIALIZER = new EntityGoalFollowMobs.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalFollowMobs> {
        @Override
        @Nullable
        public EntityGoalFollowMobs deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("minDistance")
                            && config.has("maxDistance")
            )) {
                return null;
            }

            try {
                return new EntityGoalFollowMobs(
                        config.getDouble("speed"),
                        config.getDouble("minDistance"),
                        config.getDouble("maxDistance")
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
            return NamespacedKey.minecraft("follow_mobs");
        }
    }
}
