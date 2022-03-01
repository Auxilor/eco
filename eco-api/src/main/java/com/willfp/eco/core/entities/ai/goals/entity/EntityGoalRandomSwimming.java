package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Swim around randomly.
 *
 * @param speed      The speed at which to move around.
 * @param interval   The amount of ticks to wait (on average) between strolling around.
 */
public record EntityGoalRandomSwimming(
        double speed,
        int interval
) implements EntityGoal {

}
