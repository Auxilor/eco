package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Leap at target.
 *
 * @param velocity The leap velocity.
 */
public record EntityGoalLeapAtTarget(
        double velocity
) implements EntityGoal {

}
