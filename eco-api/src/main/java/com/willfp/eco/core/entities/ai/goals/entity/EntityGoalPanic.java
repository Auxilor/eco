package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Panic.
 *
 * @param speed The speed at which to panic.
 */
public record EntityGoalPanic(
        double speed
) implements EntityGoal {

}
