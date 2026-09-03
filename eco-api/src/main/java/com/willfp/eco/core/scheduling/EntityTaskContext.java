package com.willfp.eco.core.scheduling;

import org.jetbrains.annotations.NotNull;

/**
 * Runs tasks in the context of an entity, following it between regions.
 */
public interface EntityTaskContext extends TaskContext {
    /**
     * Set the action to run if the entity is removed or unloaded before the task runs.
     * <p>
     * Returns a new context; the receiver is left unchanged. Does nothing on Paper and
     * Spigot, where tasks are not bound to an entity.
     *
     * @param onRetired The action.
     * @return A context that will use the given action.
     */
    @NotNull EntityTaskContext onRetired(@NotNull Runnable onRetired);
}
