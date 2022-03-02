package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Stroll randomly while avoiding water.
 *
 * @param speed  The speed.
 * @param chance The chance to stroll every tick, as a percentage.
 */
public record EntityGoalWaterAvoidingRandomStroll(
        double speed,
        double chance
) implements EntityGoal<Mob> {

}
