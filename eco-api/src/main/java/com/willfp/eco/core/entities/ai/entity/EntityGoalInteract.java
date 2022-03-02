package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
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
) implements EntityGoal<Mob> {

}
