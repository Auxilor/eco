package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Float in water.
 */
public record EntityGoalFloat(
) implements EntityGoal<Mob> {

}
