package com.willfp.eco.spigot.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.drops.impl.EcoDropQueue
import com.willfp.eco.internal.drops.impl.EcoFastCollatedDropQueue
import org.bukkit.scheduler.BukkitTask

class CollatedRunnable(plugin: EcoPlugin) {
    private val runnableTask: BukkitTask = plugin.scheduler.runTimer({
        for ((key, value) in EcoFastCollatedDropQueue.COLLATED_MAP) {
            EcoDropQueue(key!!)
                .setLocation(value.location)
                .addItems(value.drops)
                .addXP(value.xp)
                .push()
            EcoFastCollatedDropQueue.COLLATED_MAP.remove(key)
        }
        EcoFastCollatedDropQueue.COLLATED_MAP.clear()
    }, 0, 1)
}