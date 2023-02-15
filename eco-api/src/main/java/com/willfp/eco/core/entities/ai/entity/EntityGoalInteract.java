package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interact with other mobs.
 *
 * @param target The type of entity to interact with.
 * @param range  The range at which to interact.
 * @param chance The chance for interaction, between 0 and 1.
 */
public record EntityGoalInteract(
        @NotNull TestableEntity target,
        double range,
        double chance
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalInteract> DESERIALIZER = new EntityGoalInteract.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalInteract> {
        @Override
        @Nullable
        public EntityGoalInteract deserialize(@NotNull final Config config) {
            if (!(
                    config.has("target")
                            && config.has("range")
                            && config.has("chance")
            )) {
                return null;
            }

            return new EntityGoalInteract(
                    Entities.lookup(config.getString("target")),
                    config.getDouble("range"),
                    config.getDouble("chance")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("interact");
        }
    }
}
