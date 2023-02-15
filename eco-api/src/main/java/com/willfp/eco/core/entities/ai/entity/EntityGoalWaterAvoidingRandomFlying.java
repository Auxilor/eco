package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fly randomly while avoiding water.
 *
 * @param speed The speed.
 */
public record EntityGoalWaterAvoidingRandomFlying(
        double speed
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalWaterAvoidingRandomFlying> DESERIALIZER = new EntityGoalWaterAvoidingRandomFlying.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalWaterAvoidingRandomFlying> {
        @Override
        @Nullable
        public EntityGoalWaterAvoidingRandomFlying deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
            )) {
                return null;
            }

            return new EntityGoalWaterAvoidingRandomFlying(
                    config.getDouble("speed")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("water_avoiding_random_flying");
        }
    }
}
