package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to react when it receives damage.
 *
 * @param speed The speed at which to panic.
 */
public record EntityGoalPanic(
        double speed
) implements EntityGoal<Mob> {

}
