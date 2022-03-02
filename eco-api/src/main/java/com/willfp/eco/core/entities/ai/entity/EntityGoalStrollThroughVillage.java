package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Allows the entity to create paths around the village.
 *
 * @param searchRange The search range.
 */
public record EntityGoalStrollThroughVillage(
        int searchRange
) implements EntityGoal<Mob> {

}
