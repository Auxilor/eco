package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Monster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ranged attack.
 * <p>
 * Only supports monsters that have crossbow attacks.
 *
 * @param speed The speed.
 * @param range The max range at which to attack.
 */
public record EntityGoalRangedCrossbowAttack(
        double speed,
        double range
) implements EntityGoal<Monster> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRangedCrossbowAttack> DESERIALIZER = new EntityGoalRangedCrossbowAttack.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRangedCrossbowAttack> {
        @Override
        @Nullable
        public EntityGoalRangedCrossbowAttack deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("range")
            )) {
                return null;
            }

            return new EntityGoalRangedCrossbowAttack(
                    config.getDouble("speed"),
                    config.getDouble("range")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("ranged_crossbow_attack");
        }
    }
}
