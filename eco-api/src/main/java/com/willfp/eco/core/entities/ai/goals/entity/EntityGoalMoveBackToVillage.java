package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Move back to village.
 *
 * @param speed      The speed at which to move back to the village.
 * @param canDespawn If the entity can despawn.
 */
public record EntityGoalMoveBackToVillage(
        double speed,
        boolean canDespawn
) implements EntityGoal {

}
