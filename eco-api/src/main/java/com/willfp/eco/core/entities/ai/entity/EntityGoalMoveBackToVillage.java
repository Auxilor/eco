package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Move back to village.
 *
 * @param speed      The speed at which to move back to the village.
 * @param canDespawn If the entity can despawn.
 */
public record EntityGoalMoveBackToVillage(
        double speed,
        boolean canDespawn
) implements EntityGoal<Mob> {

}
