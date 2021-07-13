package com.willfp.eco.internal.scheduling;

import com.willfp.eco.internal.factory.EcoRunnableFactory;
import com.willfp.eco.core.scheduling.RunnableTask;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class EcoRunnableTask extends BukkitRunnable implements RunnableTask {
    /**
     * The linked {@link EcoPlugin} to associate runnables with.
     */
    private final EcoPlugin plugin;

    /**
     * Creates a new {@link EcoRunnableTask}.
     * <p>
     * Cannot be instantiated normally, use {@link EcoRunnableFactory}.
     *
     * @param plugin The {@link EcoPlugin} to associate runnables with.
     */
    @ApiStatus.Internal
    public EcoRunnableTask(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the {@link EcoPlugin} that created this runnable.
     *
     * @return The linked plugin.
     */
    protected final EcoPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTask() {
        return super.runTask(plugin);
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTaskAsynchronously() {
        return super.runTaskAsynchronously(plugin);
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTaskLater(final long delay) {
        return super.runTaskLater(plugin, delay);
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTaskLaterAsynchronously(final long delay) {
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTaskTimer(final long delay, final long period) {
        return super.runTaskTimer(plugin, delay, period);
    }

    @Override
    @NotNull
    public final synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) {
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }
}
