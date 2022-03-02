package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Move towards target.
 *
 * @param speed       The speed at which to move towards the target.
 * @param maxDistance The maximum distance the target can be where the entity will still move towards it.
 */
public record EntityGoalMoveTowardsTarget(
        double speed,
        double maxDistance
) implements EntityGoal<Mob> {

}
