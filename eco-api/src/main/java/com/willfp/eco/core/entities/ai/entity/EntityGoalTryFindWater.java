package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to move to water when on land.
 */
public record EntityGoalTryFindWater(
) implements EntityGoal<Mob> {

}
