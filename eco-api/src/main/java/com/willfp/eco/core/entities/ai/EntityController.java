package com.willfp.eco.core.entities.ai;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * An entity controller allows for adding targets and goals to entities.
 *
 * @param <T> The wrapped mob.
 */
public interface EntityController<T extends Mob> {
    /**
     * Add a target goal to the entity.
     * <p>
     * Mutates the instance.
     *
     * @param priority The priority.
     * @param goal     The goal.
     * @return The entity controller.
     */
    EntityController<T> addTargetGoal(int priority,
                                      @NotNull TargetGoal<? super T> goal);

    /**
     * Remove all target goals from the entity.
     * <p>
     * Mutates the instance.
     *
     * @return The entity controller.
     */
    EntityController<T> clearTargetGoals();

    /**
     * Remove a target goal from the entity.
     * <p>
     * Mutates the instance.
     *
     * @param goal The goal.
     * @return The entity controller.
     */
    EntityController<T> removeTargetGoal(@NotNull TargetGoal<? super T> goal);

    /**
     * Add an entity goal to the entity.
     * <p>
     * Mutates the instance.
     *
     * @param priority The priority.
     * @param goal     The goal.
     * @return The entity controller.
     */
    EntityController<T> addEntityGoal(int priority,
                                      @NotNull EntityGoal<? super T> goal);

    /**
     * Remove an entity goal from the entity.
     * <p>
     * Mutates the instance.
     *
     * @param goal The goal.
     * @return The entity controller.
     */
    EntityController<T> removeEntityGoal(@NotNull EntityGoal<? super T> goal);

    /**
     * Remove all entity goals from the entity.
     * <p>
     * Mutates the instance.
     *
     * @return The entity controller.
     */
    EntityController<T> clearEntityGoals();

    /**
     * Remove all goals from the entity.
     * <p>
     * Mutates the instance.
     *
     * @return The entity controller.
     */
    default EntityController<T> clearAllGoals() {
        this.clearTargetGoals();
        return this.clearEntityGoals();
    }

    /**
     * Get the mob back from the controlled entity.
     * <p>
     * Not required to apply changes, as the mob instance will be altered.
     *
     * @return The mob.
     */
    T getEntity();

    /**
     * Create an entity controller for an entity in order to modify targets and goals.
     *
     * @param entity The entity.
     * @param <T>    The mob type.
     * @return The entity controller.
     */
    static <T extends Mob> EntityController<T> getFor(@NotNull final T entity) {
        return Eco.get().createEntityController(entity);
    }
}
