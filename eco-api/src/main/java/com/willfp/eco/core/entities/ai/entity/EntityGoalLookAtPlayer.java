package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows an entity to look at the player by rotating the head bone pose within a set limit.
 *
 * @param range  The range at which to look at the player.
 * @param chance The chance to look at the player, as a percentage.
 */
public record EntityGoalLookAtPlayer(
        double range,
        double chance
) implements EntityGoal<Mob> {

}
