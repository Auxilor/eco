package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Move around randomly.
 *
 * @param speed      The speed at which to move around.
 * @param interval   The amount of ticks to wait (on average) between strolling around.
 * @param canDespawn If the entity can despawn.
 */
public record EntityGoalRandomStroll(
        double speed,
        int interval,
        boolean canDespawn
) implements EntityGoal<Mob> {

}
