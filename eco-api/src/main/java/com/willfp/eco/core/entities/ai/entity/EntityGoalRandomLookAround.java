package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Look around randomly.
 */
public record EntityGoalRandomLookAround(
) implements EntityGoal<Mob> {

}
