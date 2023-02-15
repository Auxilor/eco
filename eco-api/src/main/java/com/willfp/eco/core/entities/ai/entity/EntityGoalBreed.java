package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Animals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows animals to breed.
 *
 * @param speed The speed at which to move to a partner.
 */
public record EntityGoalBreed(
        double speed
) implements EntityGoal<Animals> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalBreed> DESERIALIZER = new EntityGoalBreed.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalBreed> {
        @Override
        @Nullable
        public EntityGoalBreed deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
            )) {
                return null;
            }

            return new EntityGoalBreed(
                    config.getDouble("speed")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("breed");
        }
    }
}
