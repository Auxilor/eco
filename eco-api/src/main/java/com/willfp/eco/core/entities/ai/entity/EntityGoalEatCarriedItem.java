package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to eat any item in its inventory and gain the benefits of the item.
 */
public record EntityGoalEatCarriedItem(
) implements EntityGoal<Mob> {

}
