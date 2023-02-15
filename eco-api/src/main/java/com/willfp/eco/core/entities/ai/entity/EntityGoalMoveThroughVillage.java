package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows the entity to create paths around the village.
 *
 * @param speed               The speed at which to move through the village.
 * @param onlyAtNight         If the entity can only move through village at night.
 * @param distance            The distance to move through the village.
 * @param canPassThroughDoors If the entity can pass through doors.
 */
public record EntityGoalMoveThroughVillage(
        double speed,
        boolean onlyAtNight,
        int distance,
        boolean canPassThroughDoors
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalMoveThroughVillage> DESERIALIZER = new EntityGoalMoveThroughVillage.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalMoveThroughVillage> {
        @Override
        @Nullable
        public EntityGoalMoveThroughVillage deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("onlyAtNight")
                            && config.has("distance")
                            && config.has("canPassThroughDoors")
            )) {
                return null;
            }

            return new EntityGoalMoveThroughVillage(
                    config.getDouble("speed"),
                    config.getBool("onlyAtNight"),
                    config.getInt("distance"),
                    config.getBool("canPassThroughDoors")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("move_through_village");
        }
    }
}
