package com.willfp.eco.core.entities.ai;

import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * A goal for entity target AI.
 *
 * @param <T> The type of mob that the goal can be applied to.
 */
public interface TargetGoal<T extends Mob> extends Goal<T> {
    @Override
    default T addToEntity(@NotNull T entity, int priority) {
        return EntityController.getFor(entity)
                .addTargetGoal(priority, this)
                .getEntity();
    }
}
