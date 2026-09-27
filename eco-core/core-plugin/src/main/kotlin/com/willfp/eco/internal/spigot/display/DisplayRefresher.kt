package com.willfp.eco.internal.spigot.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import java.util.concurrent.atomic.AtomicBoolean
import org.bukkit.Bukkit

class DisplayRefresher(private val plugin: EcoPlugin) {
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
}
