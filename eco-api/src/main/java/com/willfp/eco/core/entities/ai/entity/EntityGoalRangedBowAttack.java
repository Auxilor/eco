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
 * Only supports monsters that have bow attacks.
 *
 * @param speed          The speed.
 * @param attackInterval The interval between attacks (in ticks).
 * @param range          The max range at which to attack.
 */
public record EntityGoalRangedBowAttack(
        double speed,
        int attackInterval,
        double range
) implements EntityGoal<Monster> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalRangedBowAttack> DESERIALIZER = new EntityGoalRangedBowAttack.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalRangedBowAttack> {
        @Override
        @Nullable
        public EntityGoalRangedBowAttack deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("attackInterval")
                            && config.has("range")
            )) {
                return null;
            }

            try {
                return new EntityGoalRangedBowAttack(
                        config.getDouble("speed"),
                        config.getInt("attackInterval"),
                        config.getDouble("range")
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
            return NamespacedKey.minecraft("ranged_bow_attack");
        }
    }
}
