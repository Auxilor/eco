package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Target nearest attackable raider.
 *
 * @param target          The types of entities to heal.
 * @param checkVisibility If visibility should be checked.
 * @param targetFilter    The filter for targets to match.
 */
public record TargetGoalNearestHealableRaider(
        @NotNull TestableEntity target,
        boolean checkVisibility,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Raider> {
    /**
     * @param target          The target.
     * @param checkVisibility If visibility should be checked.
     */
    public TargetGoalNearestHealableRaider(@NotNull final TestableEntity target,
                                           final boolean checkVisibility) {
        this(target, checkVisibility, it -> true);
    }

    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalNearestHealableRaider> DESERIALIZER = new TargetGoalNearestHealableRaider.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalNearestHealableRaider> {
        @Override
        @Nullable
        public TargetGoalNearestHealableRaider deserialize(@NotNull final Config config) {
            if (!(
                    config.has("target")
                            && config.has("checkVisibility")
            )) {
                return null;
            }

            if (config.has("targetFilter")) {
                TestableEntity filter = Entities.lookup(config.getString("targetFilter"));

                return new TargetGoalNearestHealableRaider(
                        Entities.lookup(config.getString("target")),
                        config.getBool("checkVisibility"),
                        filter::matches
                );
            } else {
                return new TargetGoalNearestHealableRaider(
                        Entities.lookup(config.getString("target")),
                        config.getBool("checkVisibility")
                );
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("nearest_healable_raider");
        }
    }
}
