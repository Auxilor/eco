package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Flee sun.
 *
 * @param speed The speed at which to flee.
 */
public record EntityGoalFleeSun(
        double speed
) implements EntityGoal {

}
