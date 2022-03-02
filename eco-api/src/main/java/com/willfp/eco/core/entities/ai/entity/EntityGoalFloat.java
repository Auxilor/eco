package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to float on water.
 */
public record EntityGoalFloat(
) implements EntityGoal<Mob> {

}
