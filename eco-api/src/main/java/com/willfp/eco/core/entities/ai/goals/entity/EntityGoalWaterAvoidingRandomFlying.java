package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Fly randomly while avoiding water.
 *
 * @param speed The speed.
 */
public record EntityGoalWaterAvoidingRandomFlying(
        double speed
) implements EntityGoal {

}
