package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Flee sun.
 *
 * @param speed The speed at which to flee.
 */
public record EntityGoalFleeSun(
        double speed
) implements EntityGoal<Mob> {

}
