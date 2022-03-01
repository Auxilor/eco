package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Stroll through village.
 *
 * @param searchRange The search range.
 */
public record EntityGoalStrollThroughVillage(
        int searchRange
) implements EntityGoal {

}
