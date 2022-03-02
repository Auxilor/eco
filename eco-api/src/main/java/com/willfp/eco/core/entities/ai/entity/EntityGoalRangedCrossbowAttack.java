package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Monster;

/**
 * Ranged attack.
 * <p>
 * Only supports monsters that have crossbow attacks.
 *
 * @param speed          The speed.
 * @param range          The max range at which to attack.
 */
public record EntityGoalRangedCrossbowAttack(
        double speed,
        double range
) implements EntityGoal<Monster> {

}
