package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Stroll randomly while avoiding water.
 *
 * @param speed  The speed.
 * @param chance The chance to stroll every tick, as a percentage.
 */
public record EntityGoalWaterAvoidingRandomStroll(
        double speed,
        double chance
) implements EntityGoal {

}
