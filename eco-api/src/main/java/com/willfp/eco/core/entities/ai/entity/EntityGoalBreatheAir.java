package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Breathe air.
 */
public record EntityGoalBreatheAir(
) implements EntityGoal<Mob> {

}
