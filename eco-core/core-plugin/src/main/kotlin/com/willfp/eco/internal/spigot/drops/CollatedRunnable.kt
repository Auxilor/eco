package com.willfp.eco.internal.spigot.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue

class CollatedRunnable(plugin: EcoPlugin) {
    init {
        plugin.scheduler.runTimerAsync(1, 1) {
            for ((key, value) in EcoFastCollatedDropQueue.COLLATED_MAP) {
                plugin.scheduler.run(value.location) {
                    val queue = EcoDropQueue(key)
                        .setLocation(value.location)
                        .addItems(value.drops)
                        .addXP(value.xp)

                    if (value.telekinetic) {
                        queue.forceTelekinesis()
                    }

                    queue.push()

                    EcoFastCollatedDropQueue.COLLATED_MAP.remove(key)
                }
            }
            EcoFastCollatedDropQueue.COLLATED_MAP.clear()
        }
    }
}