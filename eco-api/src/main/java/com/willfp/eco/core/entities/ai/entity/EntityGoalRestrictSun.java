package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Restrict sun.
 */
public record EntityGoalRestrictSun(
) implements EntityGoal<Mob> {

}
