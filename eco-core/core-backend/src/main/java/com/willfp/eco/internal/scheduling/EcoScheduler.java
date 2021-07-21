package com.willfp.eco.internal.scheduling;

import com.willfp.eco.core.scheduling.Scheduler;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EcoScheduler extends PluginDependent<EcoPlugin> implements Scheduler {
    @ApiStatus.Internal
    public EcoScheduler(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public BukkitTask runLater(@NotNull final Runnable runnable,
                               final long ticksLater) {
        return Bukkit.getScheduler().runTaskLater(this.getPlugin(), runnable, ticksLater);
    }

    @Override
    public BukkitTask runTimer(@NotNull final Runnable runnable,
                               final long delay,
                               final long repeat) {
        return Bukkit.getScheduler().runTaskTimer(this.getPlugin(), runnable, delay, repeat);
    }

    @Override
    public BukkitTask runAsyncTimer(@NotNull final Runnable runnable,
                                    final long delay,
                                    final long repeat) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(this.getPlugin(), runnable, delay, repeat);
    }

    @Override
    public BukkitTask run(@NotNull final Runnable runnable) {
        return Bukkit.getScheduler().runTask(this.getPlugin(), runnable);
    }

    @Override
    public BukkitTask runAsync(@NotNull final Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this.getPlugin(), runnable);
    }

    @Override
    public int syncRepeating(@NotNull final Runnable runnable,
                             final long delay,
                             final long repeat) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), runnable, delay, repeat);
    }

    @Override
    public void cancelAll() {
        Bukkit.getScheduler().cancelTasks(this.getPlugin());
    }
}
