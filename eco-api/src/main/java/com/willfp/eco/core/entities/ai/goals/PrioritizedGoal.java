package com.willfp.eco.core.entities.ai.goals;

import org.jetbrains.annotations.NotNull;

/**
 * A prioritized goal is a goal that has been registered with an entity.
 *
 * @param goal     The goal.
 * @param priority The priority.
 * @param <T>      The type of goal.
 */
public record PrioritizedGoal<T>(
        @NotNull T goal,
        int priority
) {

}
