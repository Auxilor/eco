package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to jump towards a target.
 *
 * @param velocity The leap velocity.
 */
public record EntityGoalLeapAtTarget(
        double velocity
) implements EntityGoal<Mob> {

}
