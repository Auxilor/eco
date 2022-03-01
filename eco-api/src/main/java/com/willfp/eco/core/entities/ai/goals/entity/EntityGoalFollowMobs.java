package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Follow other mobs.
 *
 * @param speed       The speed at which to follow.
 * @param minDistance The minimum follow distance.
 * @param maxDistance The maximum follow distance.
 */
public record EntityGoalFollowMobs(
        double speed,
        double minDistance,
        double maxDistance
) implements EntityGoal {

}
