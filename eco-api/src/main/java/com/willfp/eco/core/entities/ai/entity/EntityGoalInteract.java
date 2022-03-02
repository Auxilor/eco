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
 * @param chance The chance for interaction, as a percentage.
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
    @SuppressWarnings("unchecked")
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

            try {
                return new EntityGoalInteract(
                        Entities.lookup(config.getString("target")),
                        config.getDouble("range"),
                        config.getDouble("chance")
                );
            } catch (Exception e) {
                /*
                Exceptions could be caused by configs having values of a wrong type,
                invalid enum parameters, etc. Serializers shouldn't throw exceptions,
                so we encapsulate them as null.
                 */
                return null;
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("interact");
        }
    }
}
