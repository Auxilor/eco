package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Move towards restriction.
 *
 * @param speed The speed at which to move towards the restriction.
 */
public record EntityGoalMoveTowardsRestriction(
        double speed
) implements EntityGoal {

}
