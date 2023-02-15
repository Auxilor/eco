package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reset universal anger.
 * <p>
 * Can only be applied to neutral mobs.
 *
 * @param triggerOthers If this should cause other nearby entities to trigger.
 */
public record TargetGoalResetUniversalAnger(
        boolean triggerOthers
) implements TargetGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalResetUniversalAnger> DESERIALIZER = new TargetGoalResetUniversalAnger.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalResetUniversalAnger> {
        @Override
        @Nullable
        public TargetGoalResetUniversalAnger deserialize(@NotNull final Config config) {
            if (!(
                    config.has("triggerOthers")
            )) {
                return null;
            }

            return new TargetGoalResetUniversalAnger(
                    config.getBool("triggerOthers")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("reset_universal_anger");
        }
    }
}
