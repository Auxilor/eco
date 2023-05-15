package com.willfp.eco.internal.spigot.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue

class CollatedRunnable(plugin: EcoPlugin) {
    init {
        plugin.scheduler.runTimer({
            if (EcoFastCollatedDropQueue.COLLATED_MAP.isNotEmpty()) {
                for ((key, value) in EcoFastCollatedDropQueue.COLLATED_MAP) {
                    val queue = EcoDropQueue(key)
                        .setLocation(value.location)
                        .addItems(value.drops)
                        .addXP(value.xp)

                    plugin.scheduler.run(value.location) {
                        if (value.telekinetic) {
                            queue.forceTelekinesis()
                        }

                        queue.push()
                    }

                    EcoFastCollatedDropQueue.COLLATED_MAP.remove(key)
                }
                EcoFastCollatedDropQueue.COLLATED_MAP.clear()
            }
        }, 1, 1)
    }
}