package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ranged attack.
 * <p>
 * Only supports mobs that have ranged attacks.
 *
 * @param speed       The speed.
 * @param minInterval The minimum interval between attacks (in ticks).
 * @param maxInterval The maximum interval between attacks (in ticks).
 * @param maxRange    The max range at which to attack.
 */
public record EntityGoalRangedAttack(
        double speed,
        int minInterval,
        int maxInterval,
        double maxRange
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRangedAttack> DESERIALIZER = new EntityGoalRangedAttack.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRangedAttack> {
        @Override
        @Nullable
        public EntityGoalRangedAttack deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("minInterval")
                            && config.has("maxInterval")
                            && config.has("range")
            )) {
                return null;
            }

            return new EntityGoalRangedAttack(
                    config.getDouble("speed"),
                    config.getInt("minInterval"),
                    config.getInt("maxInterval"),
                    config.getDouble("range")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("ranged_attack");
        }
    }
}
