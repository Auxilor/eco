package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Swim around randomly.
 *
 * @param speed      The speed at which to move around.
 * @param interval   The amount of ticks to wait (on average) between strolling around.
 */
public record EntityGoalRandomSwimming(
        double speed,
        int interval
) implements EntityGoal<Mob> {

}
