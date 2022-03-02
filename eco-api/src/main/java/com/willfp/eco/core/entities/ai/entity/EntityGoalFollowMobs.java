package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to follow and gather around all types of mobs, both hostile and neutral mobs.
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
