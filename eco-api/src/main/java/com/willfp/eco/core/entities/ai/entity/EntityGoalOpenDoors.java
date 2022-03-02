package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to interact and open a door.
 *
 * @param delayClosing If closing the door should be delayed.
 */
public record EntityGoalOpenDoors(
        boolean delayClosing
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalOpenDoors> DESERIALIZER = new EntityGoalOpenDoors.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalOpenDoors> {
        @Override
        @Nullable
        public EntityGoalOpenDoors deserialize(@NotNull final Config config) {
            if (!(
                    config.has("delayClosing")
            )) {
                return null;
            }

            try {
                return new EntityGoalOpenDoors(
                        config.getBool("delayClosing")
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
            return NamespacedKey.minecraft("open_doors");
        }
    }
}
