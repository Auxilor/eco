package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.IronGolem;

/**
 * Defend village.
 */
public record TargetGoalDefendVillage(
) implements TargetGoal<IronGolem> {

}
