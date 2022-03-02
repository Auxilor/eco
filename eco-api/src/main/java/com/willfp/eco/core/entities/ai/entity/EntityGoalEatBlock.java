package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Eat block.
 */
public record EntityGoalEatBlock(
) implements EntityGoal<Mob> {

}
