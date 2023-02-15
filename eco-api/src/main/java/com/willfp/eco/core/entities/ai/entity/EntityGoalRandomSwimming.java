package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to swim in a random point in water.
 *
 * @param speed    The speed at which to move around.
 * @param interval The amount of ticks to wait (on average) between strolling around.
 */
public record EntityGoalRandomSwimming(
        double speed,
        int interval
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRandomSwimming> DESERIALIZER = new EntityGoalRandomSwimming.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRandomSwimming> {
        @Override
        @Nullable
        public EntityGoalRandomSwimming deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("interval")
            )) {
                return null;
            }

            return new EntityGoalRandomSwimming(
                    config.getDouble("speed"),
                    config.getInt("interval")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("random_swimming");
        }
    }
}
