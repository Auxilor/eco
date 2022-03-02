package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Will make the entity actively avoid the sunlight.
 *
 * @param speed The speed at which to flee.
 */
public record EntityGoalFleeSun(
        double speed
) implements EntityGoal<Mob> {

}
