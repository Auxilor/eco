package com.willfp.eco.internal.spigot.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue.CollatedDrops
import org.bukkit.entity.Player

/**
 * Take every pending entry and hand it to [dispatch].
 *
 * Entries are removed one at a time, as they are taken, and never in bulk: another thread
 * can add a drop while this is running, and clearing the map afterwards would discard it.
 * The remove is also what claims an entry, so no entry is dispatched twice.
 */
internal fun drainCollatedDrops(
    map: MutableMap<Player, CollatedDrops>,
    dispatch: (Player, CollatedDrops) -> Unit
) {
    for (player in map.keys.toList()) {
        val drops = map.remove(player) ?: continue
        dispatch(player, drops)
    }
}

/**
 * Pushes collated drops once a tick.
 *
 * The drain runs globally, but each queue is pushed on the region owning the drop
 * location, because pushing spawns items and experience orbs into the world.
 */
class CollatedRunnable(plugin: EcoPlugin) {
    init {
        plugin.scheduler.global().runTimer(0, 1) {
            drainCollatedDrops(EcoFastCollatedDropQueue.COLLATED_MAP) { player, drops ->
                val location = drops.location

                plugin.scheduler.at(location).run {
                    val queue = EcoDropQueue(player)
                        .setLocation(location)
                        .addItems(drops.drops)
                        .addXP(drops.xp)

                    if (drops.telekinetic) {
                        queue.forceTelekinesis()
                    }

                    queue.push()
                }
            }
        }
    }
}
