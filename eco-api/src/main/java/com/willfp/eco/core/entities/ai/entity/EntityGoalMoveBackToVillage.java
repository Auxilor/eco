package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to navigate and search for a nearby village.
 *
 * @param speed      The speed at which to move back to the village.
 * @param canDespawn If the entity can despawn.
 */
public record EntityGoalMoveBackToVillage(
        double speed,
        boolean canDespawn
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalMoveBackToVillage> DESERIALIZER = new EntityGoalMoveBackToVillage.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalMoveBackToVillage> {
        @Override
        @Nullable
        public EntityGoalMoveBackToVillage deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("canDespawn")
            )) {
                return null;
            }

            return new EntityGoalMoveBackToVillage(
                    config.getDouble("speed"),
                    config.getBool("canDespawn")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("move_back_to_village");
        }
    }
}
