package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

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
) implements EntityGoal<Mob> {

}
