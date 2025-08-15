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

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Allows an entity to attack the closest target within a given subset of specific target types.
 *
 * @param targets          The type of entities to attack.
 * @param checkVisibility  If visibility should be checked.
 * @param checkCanNavigate If navigation should be checked.
 * @param reciprocalChance 1 in reciprocalChance chance of not activating on any tick.
 * @param targetFilter     The filter for targets to match.
 */
public record TargetGoalNearestAttackable(
        @NotNull Set<TestableEntity> targets,
        boolean checkVisibility,
        boolean checkCanNavigate,
        int reciprocalChance,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Raider> {
    /**
     * Create a new target goal.
     *
     * @param targets          The type of entities to attack.
     * @param checkVisibility  If visibility should be checked.
     * @param checkCanNavigate If navigation should be checked.
     * @param reciprocalChance 1 in reciprocalChance chance of not activating on any tick.
     */
    public TargetGoalNearestAttackable(@NotNull final Set<TestableEntity> targets,
                                       final boolean checkVisibility,
                                       final boolean checkCanNavigate,
                                       final int reciprocalChance) {
        this(targets, checkVisibility, checkCanNavigate, reciprocalChance, it -> true);
    }

    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalNearestAttackable> DESERIALIZER = new TargetGoalNearestAttackable.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<TargetGoalNearestAttackable> {
        @Override
        @Nullable
        public TargetGoalNearestAttackable deserialize(@NotNull final Config config) {
            if (!(
                    config.has("target")
                            && config.has("checkVisibility")
                            && config.has("checkCanNavigate")
                            && config.has("reciprocalChance")
            )) {
                return null;
            }

            Set<TestableEntity> targets = config.getStrings("target").stream()
                    .map(Entities::lookup)
                    .collect(Collectors.toSet());

            if (config.has("targetFilter")) {
                TestableEntity filter = Entities.lookup(config.getString("targetFilter"));

                return new TargetGoalNearestAttackable(
                        targets,
                        config.getBool("checkVisibility"),
                        config.getBool("checkCanNavigate"),
                        config.getInt("reciprocalChance"),
                        filter::matches
                );
            } else {
                return new TargetGoalNearestAttackable(
                        targets,
                        config.getBool("checkVisibility"),
                        config.getBool("checkCanNavigate"),
                        config.getInt("reciprocalChance")
                );
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("nearest_attackable");
        }
    }
}
