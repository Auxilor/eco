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
 * @param chance The chance to look at the player, between 0 and 1.
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

            return new EntityGoalLookAtPlayer(
                    config.getDouble("range"),
                    config.getDouble("chance")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("look_at_player");
        }
    }
}
