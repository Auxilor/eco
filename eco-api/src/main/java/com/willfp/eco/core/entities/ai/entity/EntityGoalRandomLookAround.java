package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Allows an entity to choose a random direction to look in for a random duration within a range.
 */
public record EntityGoalRandomLookAround(
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRandomLookAround> DESERIALIZER = new EntityGoalRandomLookAround.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRandomLookAround> {
        @Override
        public EntityGoalRandomLookAround deserialize(@NotNull final Config config) {
            return new EntityGoalRandomLookAround();
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("random_look_around");
        }
    }
}
