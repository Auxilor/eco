package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Move towards target.
 *
 * @param speed       The speed at which to move towards the target.
 * @param maxDistance The maximum distance the target can be where the entity will still move towards it.
 */
public record EntityGoalMoveTowardsTarget(
        double speed,
        double maxDistance
) implements EntityGoal {

}
