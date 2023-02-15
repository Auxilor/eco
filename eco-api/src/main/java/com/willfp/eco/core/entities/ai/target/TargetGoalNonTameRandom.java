package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Target random non-tame entity.
 *
 * @param target          The types of entities to heal.
 * @param checkVisibility If visibility should be checked.
 * @param targetFilter    The filter for targets to match.
 */
public record TargetGoalNonTameRandom(
        @NotNull TestableEntity target,
        boolean checkVisibility,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Tameable> {
    /**
     * @param target          The types of entities to heal.
     * @param checkVisibility If visibility should be checked.
     */
    public TargetGoalNonTameRandom(@NotNull final TestableEntity target,
                                   final boolean checkVisibility) {
        this(target, checkVisibility, it -> true);
    }

    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalNonTameRandom> DESERIALIZER = new TargetGoalNonTameRandom.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalNonTameRandom> {
        @Override
        @Nullable
        public TargetGoalNonTameRandom deserialize(@NotNull final Config config) {
            if (!(
                    config.has("targetClass")
                            && config.has("checkVisibility")
            )) {
                return null;
            }

            if (config.has("targetFilter")) {
                TestableEntity filter = Entities.lookup(config.getString("targetFilter"));

                return new TargetGoalNonTameRandom(
                        Entities.lookup(config.getString("target")),
                        config.getBool("checkVisibility"),
                        filter::matches
                );
            } else {
                return new TargetGoalNonTameRandom(
                        Entities.lookup(config.getString("target")),
                        config.getBool("checkVisibility")
                );
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("non_tame_random");
        }
    }
}
