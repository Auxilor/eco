package com.willfp.eco.core.entities.ai;

import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all custom goals.
 * <p>
 * Can be used both for entity goals and target goals.
 *
 * @param <T> The type of mob that this goal can be applied to.
 */
public interface CustomGoal<T extends Mob> extends EntityGoal<T>, TargetGoal<T> {
    /**
     * Initialize the goal with a mob.
     * <p>
     * This will be run before any implementation code, treat this as the constructor.
     *
     * @param mob The mob.
     */
    void initialize(@NotNull T mob);

    /**
     * Get if the goal can be used.
     * Will start the goal if this returns true.
     *
     * @return If the goal can be used.
     */
    boolean canUse();

    /**
     * Tick the goal.
     * <p>
     * Runs ever tick as long as {@link this#canUse()} returns true.
     * <p>
     * Runs after {@link this#start()}.
     */
    default void tick() {
        // Override when needed.
    }

    /**
     * Start the goal.
     * <p>
     * Runs once {@link this#canUse()} returns true.
     */
    default void start() {
        // Override when needed.
    }

    /**
     * Stop the goal.
     * <p>
     * Runs once {@link this#canUse()} returns false.
     */
    default void stop() {
        // Override when needed.
    }
}
