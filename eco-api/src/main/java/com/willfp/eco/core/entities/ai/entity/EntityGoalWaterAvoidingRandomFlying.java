package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Fly randomly while avoiding water.
 *
 * @param speed The speed.
 */
public record EntityGoalWaterAvoidingRandomFlying(
        double speed
) implements EntityGoal<Mob> {

}
