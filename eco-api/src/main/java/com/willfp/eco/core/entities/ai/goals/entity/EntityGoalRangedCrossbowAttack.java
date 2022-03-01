package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Ranged attack.
 * <p>
 * Only supports monsters that have crossbow attacks.
 *
 * @param speed          The speed.
 * @param range          The max range at which to attack.
 */
public record EntityGoalRangedCrossbowAttack(
        double speed,
        double range
) implements EntityGoal {

}
