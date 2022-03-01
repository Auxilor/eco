package com.willfp.eco.core.entities.ai;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.entities.ai.goals.EntityGoal;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * A controlled entity allows for adding targets and goals to entities.
 */
public interface ControlledEntity {
    /**
     * Add a target to the entity.
     *
     * @param priority The priority.
     * @param goal     The goal.
     * @return The entity.
     */
    ControlledEntity addTarget(int priority,
                               @NotNull EntityGoal goal);

    /**
     * Add a goal to the entity.
     *
     * @param priority The priority.
     * @param goal     The goal.
     * @return The entity.
     */
    ControlledEntity addGoal(int priority,
                             @NotNull EntityGoal goal);

    /**
     * Get the mob back from the controlled entity.
     *
     * @return The mob.
     */
    Mob getEntity();

    /**
     * Wrap an entity into a controlled entity in order to modify targets and goals.
     *
     * @param entity The entity.
     * @return The controlled entity.
     */
    static ControlledEntity from(@NotNull final Mob entity) {
        return Eco.getHandler().createControlledEntity(entity);
    }
}
