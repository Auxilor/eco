package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to break down doors.
 *
 * @param maxProgress The time taken to break the door (any integer above 240).
 */
public record EntityGoalBreakDoor(
        int maxProgress
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalBreakDoor> DESERIALIZER = new EntityGoalBreakDoor.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalBreakDoor> {
        @Override
        @Nullable
        public EntityGoalBreakDoor deserialize(@NotNull final Config config) {
            if (!(
                    config.has("maxProgress")
            )) {
                return null;
            }

            try {
                return new EntityGoalBreakDoor(
                        config.getInt("maxProgress")
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
            return NamespacedKey.minecraft("break_door");
        }
    }
}
