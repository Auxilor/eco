package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to look at the player by rotating the head bone pose within a set limit.
 *
 * @param range  The range at which to look at the player.
 * @param chance The chance to look at the player, as a percentage.
 */
public record EntityGoalLookAtPlayer(
        double range,
        double chance
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalLookAtPlayer> DESERIALIZER = new EntityGoalLookAtPlayer.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalLookAtPlayer> {
        @Override
        @Nullable
        public EntityGoalLookAtPlayer deserialize(@NotNull final Config config) {
            if (!(
                    config.has("range")
                            && config.has("chance")
            )) {
                return null;
            }

            try {
                return new EntityGoalLookAtPlayer(
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
            return NamespacedKey.minecraft("look_at_player");
        }
    }
}
