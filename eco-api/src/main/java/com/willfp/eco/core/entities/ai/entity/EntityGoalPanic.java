package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to react when it receives damage.
 *
 * @param speed The speed at which to panic.
 */
public record EntityGoalPanic(
        double speed
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalPanic> DESERIALIZER = new EntityGoalPanic.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalPanic> {
        @Override
        @Nullable
        public EntityGoalPanic deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
            )) {
                return null;
            }

            return new EntityGoalPanic(
                    config.getDouble("speed")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("panic");
        }
    }
}
