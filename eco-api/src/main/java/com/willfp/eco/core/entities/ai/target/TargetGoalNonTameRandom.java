package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Target random non-tame entity.
 *
 * @param targetClass     The types of entities to heal.
 * @param checkVisibility If visibility should be checked.
 * @param targetFilter    The filter for targets to match.
 */
public record TargetGoalNonTameRandom(
        @NotNull Class<? extends LivingEntity> targetClass,
        boolean checkVisibility,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Tameable> {

}
