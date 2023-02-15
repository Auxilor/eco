package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows entities to make close combat melee attacks.
 *
 * @param speed            The speed at which to attack the target.
 * @param pauseWhenMobIdle If the entity should pause attacking when the target is idle.
 */
public record EntityGoalMeleeAttack(
        double speed,
        boolean pauseWhenMobIdle
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalMeleeAttack> DESERIALIZER = new EntityGoalMeleeAttack.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalMeleeAttack> {
        @Override
        @Nullable
        public EntityGoalMeleeAttack deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("pauseWhenMobIdle")
            )) {
                return null;
            }

            return new EntityGoalMeleeAttack(
                    config.getDouble("speed"),
                    config.getBool("pauseWhenMobIdle")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("melee_attack");
        }
    }
}
