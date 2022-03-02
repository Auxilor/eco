package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Leap at target.
 *
 * @param velocity The leap velocity.
 */
public record EntityGoalLeapAtTarget(
        double velocity
) implements EntityGoal<Mob> {

}
