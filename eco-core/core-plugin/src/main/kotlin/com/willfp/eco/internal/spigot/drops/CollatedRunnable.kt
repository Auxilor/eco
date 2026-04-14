package com.willfp.eco.internal.spigot.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue
import org.bukkit.Bukkit

class CollatedRunnable(plugin: EcoPlugin) {
    init {
        plugin.scheduler.runTimer({
            val snapshot = HashMap(EcoFastCollatedDropQueue.COLLATED_MAP)
            for ((uuid, value) in snapshot) {
                val player = Bukkit.getPlayer(uuid) ?: continue
                val queue = EcoDropQueue(player)
                    .setLocation(value.location)
                    .addItems(value.drops)
                    .addXP(value.xp)

                if (value.telekinetic) {
                    queue.forceTelekinesis()
                }

                queue.push()

                EcoFastCollatedDropQueue.COLLATED_MAP.remove(uuid, value)
            }
        }, 0, 1)
    }
}