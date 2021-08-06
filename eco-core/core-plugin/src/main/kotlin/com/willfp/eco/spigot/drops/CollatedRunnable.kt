package com.willfp.eco.spigot.drops;


import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.internal.drops.impl.EcoDropQueue;
import com.willfp.eco.internal.drops.impl.EcoFastCollatedDropQueue;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CollatedRunnable {
    @Getter
    private final BukkitTask runnableTask;
    
    public CollatedRunnable(@NotNull final EcoPlugin plugin) {
        runnableTask = plugin.getScheduler().runTimer(() -> {
            for (Map.Entry<Player, EcoFastCollatedDropQueue.CollatedDrops> entry : EcoFastCollatedDropQueue.COLLATED_MAP.entrySet()) {
                new EcoDropQueue(entry.getKey())
                        .setLocation(entry.getValue().getLocation())
                        .addItems(entry.getValue().getDrops())
                        .addXP(entry.getValue().getXp())
                        .push();
                EcoFastCollatedDropQueue.COLLATED_MAP.remove(entry.getKey());
            }
            EcoFastCollatedDropQueue.COLLATED_MAP.clear();
        }, 0, 1);
    }
}
