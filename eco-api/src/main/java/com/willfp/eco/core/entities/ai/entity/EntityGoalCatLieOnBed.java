package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Cat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows a cat to lie on a bed.
 *
 * @param speed The speed at which to move to the bed.
 * @param range The range at which to search for beds.
 */
public record EntityGoalCatLieOnBed(
        double speed,
        int range
) implements EntityGoal<Cat> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalCatLieOnBed> DESERIALIZER = new EntityGoalCatLieOnBed.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalCatLieOnBed> {
        @Override
        @Nullable
        public EntityGoalCatLieOnBed deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("range")
            )) {
                return null;
            }

            return new EntityGoalCatLieOnBed(
                    config.getDouble("speed"),
                    config.getInt("range")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("cat_lie_on_bed");
        }
    }
}
