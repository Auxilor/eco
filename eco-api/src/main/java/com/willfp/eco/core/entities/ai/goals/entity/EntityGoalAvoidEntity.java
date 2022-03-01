package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Avoid entities.
 *
 * @param avoidClass   The entity classes to avoid.
 * @param fleeDistance The distance to flee to.
 * @param slowSpeed    The slow movement speed.
 * @param fastSpeed    The fast movement speed.
 * @param filter       The filter for entities to check if they should be avoided.
 */
public record EntityGoalAvoidEntity(
        @NotNull Class<? extends LivingEntity> avoidClass,
        double fleeDistance,
        double slowSpeed,
        double fastSpeed,
        @NotNull Predicate<LivingEntity> filter
) implements EntityGoal {

}
