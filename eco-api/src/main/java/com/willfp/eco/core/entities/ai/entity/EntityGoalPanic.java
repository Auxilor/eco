package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Panic.
 *
 * @param speed The speed at which to panic.
 */
public record EntityGoalPanic(
        double speed
) implements EntityGoal<Mob> {

}
