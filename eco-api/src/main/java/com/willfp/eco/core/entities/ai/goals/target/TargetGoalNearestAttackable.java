package com.willfp.eco.core.entities.ai.goals.target;

import com.willfp.eco.core.entities.ai.goals.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Target nearest attackable entity.
 *
 * @param targetClass     The types of entities to attack.
 * @param checkVisibility If visibility should be checked.
 */
public record TargetGoalNearestAttackable(
        @NotNull Class<? extends LivingEntity> targetClass,
        boolean checkVisibility
) implements TargetGoal {

}
