package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;

/**
 * Allows a tameable entity to target whatever hurt its owner.
 */
public record TargetGoalOwnerHurtBy(
) implements TargetGoal<Tameable> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalOwnerHurtBy> DESERIALIZER = new TargetGoalOwnerHurtBy.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalOwnerHurtBy> {
        @Override
        public TargetGoalOwnerHurtBy deserialize(@NotNull final Config config) {
            return new TargetGoalOwnerHurtBy();
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("owner_hurt_by");
        }
    }
}
