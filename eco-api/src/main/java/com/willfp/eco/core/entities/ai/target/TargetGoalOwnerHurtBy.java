package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.Tameable;

/**
 * Allows an entity to react when the owner is hit by a target.
 */
public record TargetGoalOwnerHurtBy(
) implements TargetGoal<Tameable> {

}
