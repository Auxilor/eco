package com.willfp.eco.core.entities.ai;

import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * A generic goal for entity AI.
 *
 * @param <T> The type of mob that the goal can be applied to.
 */
public interface Goal<T extends Mob> {
    /**
     * Add the entity goal to an entity.
     * <p>
     * The lower the priority, the higher up the execution order; so
     * priority 0 will execute first. Lower priority (higher number) goals
     * will only execute if all higher priority goals are stopped.
     *
     * @param entity   The entity.
     * @param priority The priority.
     * @return The entity, modified.
     */
    T addToEntity(@NotNull T entity, int priority);
}
