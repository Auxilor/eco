package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Move towards restriction.
 *
 * @param speed The speed at which to move towards the restriction.
 */
public record EntityGoalMoveTowardsRestriction(
        double speed
) implements EntityGoal<Mob> {

}
