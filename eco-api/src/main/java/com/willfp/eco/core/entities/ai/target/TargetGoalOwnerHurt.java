package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;

/**
 * Allows an entity to react when the owner hits a target.
 */
public record TargetGoalOwnerHurt(
) implements TargetGoal<Tameable> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalOwnerHurt> DESERIALIZER = new TargetGoalOwnerHurt.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalOwnerHurt> {
        @Override
        public TargetGoalOwnerHurt deserialize(@NotNull final Config config) {
            return new TargetGoalOwnerHurt();
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("owner_hurt");
        }
    }
}
