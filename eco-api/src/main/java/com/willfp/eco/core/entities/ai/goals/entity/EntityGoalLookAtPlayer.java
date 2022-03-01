package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Look at the player.
 *
 * @param range  The range at which to look at the player.
 * @param chance The chance to look at the player, as a percentage.
 */
public record EntityGoalLookAtPlayer(
        double range,
        double chance
) implements EntityGoal {

}
