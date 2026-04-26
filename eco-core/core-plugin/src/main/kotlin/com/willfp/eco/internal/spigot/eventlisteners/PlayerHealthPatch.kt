package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.Eco
import com.willfp.eco.util.saveHealth
import com.willfp.eco.util.savedHealth
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerHealthPatch: Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun handlePlayerQuit(event: PlayerQuitEvent) {
        event.player.saveHealth()
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        if (Eco.get().ecoPlugin.configYml.getBool("enable-health-fix")) {
            Eco.get().ecoPlugin.scheduler.runLater(5L) {
                event.player.health = event.player.savedHealth
            }
        }
    }
}