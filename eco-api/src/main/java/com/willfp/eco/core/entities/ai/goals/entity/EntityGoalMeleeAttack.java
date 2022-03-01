package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Melee attack target.
 *
 * @param speed            The speed at which to attack the target.
 * @param pauseWhenMobIdle If the entity should pause attacking when the target is idle.
 */
public record EntityGoalMeleeAttack(
        double speed,
        boolean pauseWhenMobIdle
) implements EntityGoal {

}
