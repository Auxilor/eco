package com.willfp.eco.spigot.drops;


import com.willfp.eco.internal.drops.impl.FastCollatedDropQueue;
import com.willfp.eco.internal.drops.impl.InternalDropQueue;
import com.willfp.eco.core.EcoPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CollatedRunnable {
    /**
     * The {@link BukkitTask} that the runnable represents.
     */
    @Getter
    private final BukkitTask runnableTask;

    /**
     * Create and run a new runnable to process collated drops.
     *
     * @param plugin The {@link EcoPlugin} that manages the processing.
     */
    public CollatedRunnable(@NotNull final EcoPlugin plugin) {
        runnableTask = plugin.getScheduler().runTimer(() -> {
            for (Map.Entry<Player, FastCollatedDropQueue.CollatedDrops> entry : FastCollatedDropQueue.COLLATED_MAP.entrySet()) {
                new InternalDropQueue(entry.getKey())
                        .setLocation(entry.getValue().getLocation())
                        .addItems(entry.getValue().getDrops())
                        .addXP(entry.getValue().getXp())
                        .push();
                FastCollatedDropQueue.COLLATED_MAP.remove(entry.getKey());
            }
            FastCollatedDropQueue.COLLATED_MAP.clear();
        }, 0, 1);
    }
}
