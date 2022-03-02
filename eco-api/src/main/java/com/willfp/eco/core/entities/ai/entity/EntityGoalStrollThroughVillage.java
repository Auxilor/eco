package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Stroll through village.
 *
 * @param searchRange The search range.
 */
public record EntityGoalStrollThroughVillage(
        int searchRange
) implements EntityGoal<Mob> {

}
