package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.Tameable;

/**
 * Target last entity that hurt the owner.
 */
public record TargetGoalOwnerHurt(
) implements TargetGoal<Tameable> {

}
