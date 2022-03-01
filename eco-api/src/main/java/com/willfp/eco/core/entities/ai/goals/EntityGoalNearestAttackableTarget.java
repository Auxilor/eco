package com.willfp.eco.core.entities.ai.goals;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record EntityGoalNearestAttackableTarget(
        @NotNull Class<? extends LivingEntity> targetClass,
        boolean checkVisibility
) implements EntityGoal {

}
