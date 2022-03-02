package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to choose a random direction to walk towards.
 *
 * @param speed      The speed at which to move around.
 * @param interval   The amount of ticks to wait (on average) between strolling around.
 * @param canDespawn If the entity can despawn.
 */
public record EntityGoalRandomStroll(
        double speed,
        int interval,
        boolean canDespawn
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRandomStroll> DESERIALIZER = new EntityGoalRandomStroll.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRandomStroll> {
        @Override
        @Nullable
        public EntityGoalRandomStroll deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("interval")
                            && config.has("canDespawn")
            )) {
                return null;
            }

            try {
                return new EntityGoalRandomStroll(
                        config.getDouble("speed"),
                        config.getInt("interval"),
                        config.getBool("canDespawn")
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
            return NamespacedKey.minecraft("random_stroll");
        }
    }
}
