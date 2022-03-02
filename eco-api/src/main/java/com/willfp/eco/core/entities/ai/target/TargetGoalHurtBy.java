package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Hurt by entity.
 *
 * @param blacklist The entities not to attack when hurt by.
 */
@SuppressWarnings({"varargs", "unchecked"})
public record TargetGoalHurtBy(
        @NotNull Class<? extends LivingEntity>... blacklist
) implements TargetGoal<Mob> {

}
