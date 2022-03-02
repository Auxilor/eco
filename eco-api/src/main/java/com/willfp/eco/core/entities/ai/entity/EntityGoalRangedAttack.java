package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Ranged attack.
 * <p>
 * Only supports mobs that have ranged attacks.
 *
 * @param mobSpeed    The mob speed.
 * @param minInterval The minimum interval between attacks (in ticks).
 * @param maxInterval The maximum interval between attacks (in ticks).
 * @param maxRange    The max range at which to attack.
 */
public record EntityGoalRangedAttack(
        double mobSpeed,
        int minInterval,
        int maxInterval,
        double maxRange
) implements EntityGoal<Mob> {

}
