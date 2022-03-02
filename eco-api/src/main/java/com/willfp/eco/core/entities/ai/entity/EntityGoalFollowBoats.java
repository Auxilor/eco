package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Follow boats.
 */
public record EntityGoalFollowBoats(
) implements EntityGoal<Mob> {

}
