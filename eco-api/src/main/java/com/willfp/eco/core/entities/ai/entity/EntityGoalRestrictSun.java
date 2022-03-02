package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to actively avoid direct sunlight.
 */
public record EntityGoalRestrictSun(
) implements EntityGoal<Mob> {

}
