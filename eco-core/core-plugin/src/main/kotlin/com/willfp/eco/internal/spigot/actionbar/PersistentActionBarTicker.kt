package com.willfp.eco.internal.spigot.actionbar

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Ticks each online player's persistent action bar on the player's own thread.
 */
class PersistentActionBarTicker(
    private val plugin: EcoPlugin,
    private val service: PersistentActionBarService
) : Listener {
    private val tasks = ConcurrentHashMap<UUID, EcoTask>()

    fun startAll() {
        for (player in Bukkit.getOnlinePlayers()) {
            start(player)
        }
    }

    private fun start(player: Player) {
        tasks.put(
            player.uniqueId,
            plugin.scheduler.on(player).runTimer(
                PersistentActionBarService.TICK_INTERVAL,
                PersistentActionBarService.TICK_INTERVAL
            ) { service.tick(player) }
        )?.cancel()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        start(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        tasks.remove(event.player.uniqueId)?.cancel()
        service.remove(event.player)
    }
}
