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
 * @param searchRange The search range.
 */
public record EntityGoalStrollThroughVillage(
        int searchRange
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalStrollThroughVillage> DESERIALIZER = new EntityGoalStrollThroughVillage.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalStrollThroughVillage> {
        @Override
        @Nullable
        public EntityGoalStrollThroughVillage deserialize(@NotNull final Config config) {
            if (!(
                    config.has("searchRange")
            )) {
                return null;
            }

            return new EntityGoalStrollThroughVillage(
                    config.getInt("searchRange")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("stroll_through_village");
        }
    }
}
