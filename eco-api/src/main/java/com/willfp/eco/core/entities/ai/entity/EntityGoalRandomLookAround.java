package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to choose a random direction to look in for a random duration within a range.
 */
public record EntityGoalRandomLookAround(
) implements EntityGoal<Mob> {

}
