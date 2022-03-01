package com.willfp.eco.core.entities.ai.goals.target;

import com.willfp.eco.core.entities.ai.goals.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Hurt by entity.
 *
 * @param blacklist The entities not to attack when hurt by.
 */
public record TargetGoalHurtBy(
        @NotNull Class<? extends LivingEntity>... blacklist
) implements TargetGoal {
    /**
     * Hurt by entity.
     *
     * @param blacklist The entities not to attack when hurt by.
     */
    @SafeVarargs
    public TargetGoalHurtBy {
    }
}
