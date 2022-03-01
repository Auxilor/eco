package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Interact with other mobs.
 *
 * @param targetClass The type of entity to interact with.
 * @param range       The range at which to interact.
 * @param chance      The chance for interaction, as a percentage.
 */
public record EntityGoalInteract(
        @NotNull Class<? extends LivingEntity> targetClass,
        double range,
        double chance
) implements EntityGoal {

}
