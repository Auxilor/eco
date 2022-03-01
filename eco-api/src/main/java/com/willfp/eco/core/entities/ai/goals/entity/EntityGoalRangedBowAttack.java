package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Ranged attack.
 * <p>
 * Only supports monsters that have bow attacks.
 *
 * @param speed          The speed.
 * @param attackInterval The interval between attacks (in ticks).
 * @param range          The max range at which to attack.
 */
public record EntityGoalRangedBowAttack(
        double speed,
        int attackInterval,
        double range
) implements EntityGoal {

}
