package com.willfp.eco.core.entities.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all custom goals.
 * <p>
 * Can be used both for entity goals and target goals.
 *
 * @param <T> The type of mob that this goal can be applied to.
 */
public abstract class CustomGoal<T extends Mob> implements EntityGoal<T>, TargetGoal<T> {
    /**
     * The flags for the goal.
     */
    private final Set<GoalFlag> flags = EnumSet.noneOf(GoalFlag.class);

    /**
     * Create a new custom goal.
     */
    protected CustomGoal() {

    }

    /**
     * Initialize the goal with a mob.
     * <p>
     * This will be run before any implementation code, treat this as the constructor.
     *
     * @param mob The mob.
     */
    public abstract void initialize(@NotNull T mob);

    /**
     * Get if the goal can be used.
     * <p>
     * The goal will be started if this returns true.
     *
     * @return If the goal can be used.
     */
    public abstract boolean canUse();

    /**
     * Tick the goal.
     * <p>
     * Runs every tick as long as {@link #canContinueToUse()} returns true.
     * <p>
     * Runs after {@link #start()}.
     */
    public void tick() {
        // Override when needed.
    }

    /**
     * Start the goal.
     * <p>
     * Runs once {@link #canUse()} returns true.
     */
    public void start() {
        // Override when needed.
    }

    /**
     * Stop the goal.
     * <p>
     * Runs once {@link #canContinueToUse()} returns false.
     */
    public void stop() {
        // Override when needed.
    }

    /**
     * Get if the goal can continue to be used.
     * <p>
     * Defaults to {@link #canUse()}.
     *
     * @return If the goal can continue to be used.
     */
    public boolean canContinueToUse() {
        return this.canUse();
    }

    /**
     * Get if the goal is interruptable.
     *
     * @return If interruptable.
     */
    public boolean isInterruptable() {
        return true;
    }

    /**
     * Get the goal flags.
     *
     * @return A copy of the flags.
     */
    public EnumSet<GoalFlag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }

    /**
     * Set the flags for the goal.
     *
     * @param flags The flags.
     */
    public final void setFlags(@NotNull final GoalFlag... flags) {
        this.setFlags(EnumSet.copyOf(List.of(flags)));
    }

    /**
     * Set the flags for the goal.
     *
     * @param flags The flags.
     */
    public void setFlags(@NotNull final EnumSet<GoalFlag> flags) {
        this.flags.clear();
        this.flags.addAll(flags);
    }

    @Override
    public T addToEntity(@NotNull final T entity,
                         final int priority) {
        throw new UnsupportedOperationException(
                "Shorthand syntax is not supported for custom goals by default as they can be both entity and target goals."
        );
    }
}
