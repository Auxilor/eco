package com.willfp.eco.core.entities.ai;

import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * A goal for entity AI.
 *
 * @param <T> The type of mob that the goal can be applied to.
 */
public interface EntityGoal<T extends Mob> extends Goal<T> {
    @Override
    default T addToEntity(@NotNull T entity, int priority) {
        return EntityController.getFor(entity)
                .addEntityGoal(priority, this)
                .getEntity();
    }
}
