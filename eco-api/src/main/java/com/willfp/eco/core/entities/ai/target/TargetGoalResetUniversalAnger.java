package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.Mob;

/**
 * Reset universal anger.
 * <p>
 * Can only be applied to neutral mobs.
 *
 * @param triggerOthers If this should cause other nearby entities to trigger.
 */
public record TargetGoalResetUniversalAnger(
        boolean triggerOthers
) implements TargetGoal<Mob> {

}
