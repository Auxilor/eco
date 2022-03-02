package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Try to find water.
 */
public record EntityGoalTryFindWater(
) implements EntityGoal<Mob> {

}
