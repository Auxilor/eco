package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows a wolf to beg.
 *
 * @param distance The distance at which to beg from.
 */
public record EntityGoalWolfBeg(
        double distance
) implements EntityGoal<Wolf> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalWolfBeg> DESERIALIZER = new EntityGoalWolfBeg.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalWolfBeg> {
        @Override
        @Nullable
        public EntityGoalWolfBeg deserialize(@NotNull final Config config) {
            if (!(
                    config.has("distance")
            )) {
                return null;
            }

            return new EntityGoalWolfBeg(
                    config.getDouble("distance")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("wolf_beg");
        }
    }
}
