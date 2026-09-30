package com.willfp.eco.internal.spigot.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import java.util.concurrent.atomic.AtomicBoolean
import org.bukkit.Bukkit
import org.bukkit.entity.Player

private const val MIN_RECOMMENDED_INTERVAL = 20L

class DisplayRefresher(
    private val plugin: EcoPlugin,
    private val clearDisplayFrame: (Player) -> Unit
) {
    private val pending = AtomicBoolean(false)

    fun request() {
        if (!plugin.isEnabled || !plugin.configYml.getBool("display-refresh-on-reload")) {
            return
        }

        pending.set(true)

        plugin.scheduler.global().runLater(1) {
            if (pending.compareAndSet(true, false)) {
                refresh()
            }
        }
    }

    fun refresh() {
        Display.invalidate()

        for (player in Bukkit.getOnlinePlayers()) {
            plugin.scheduler.on(player).run { player.updateInventory() }
        }
    }

    fun startPeriodicRefresh(interval: Long) {
        if (interval < MIN_RECOMMENDED_INTERVAL) {
            plugin.logger.warning(
                "display-refresh-interval is $interval ticks. Every player's inventory is sent again once per " +
                        "interval, so intervals under $MIN_RECOMMENDED_INTERVAL ticks can cost a lot of performance."
            )
        }

        var tick = 0L

        plugin.scheduler.global().runTimer(1L, 1L) {
            val bucket = tick++ % interval

            for (player in Bukkit.getOnlinePlayers()) {
                if (Math.floorMod(player.uniqueId.hashCode().toLong(), interval) == bucket) {
                    clearDisplayFrame(player)
                    plugin.scheduler.on(player).run { player.updateInventory() }
                }
            }
        }
    }
}
