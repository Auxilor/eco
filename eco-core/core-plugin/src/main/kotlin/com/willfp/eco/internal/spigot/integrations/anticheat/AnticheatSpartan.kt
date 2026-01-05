package com.willfp.eco.internal.spigot.integrations.anticheat

import ai.idealistic.spartan.api.PlayerViolationEvent
import com.willfp.eco.core.integrations.anticheat.AnticheatIntegration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.UUID

class AnticheatSpartan : AnticheatIntegration, Listener {
    private val exempt: MutableSet<UUID> = HashSet()
    override fun getPluginName(): String {
        return "Spartan"
    }

    override fun exempt(player: Player) {
        exempt.add(player.uniqueId)
    }

    override fun unexempt(player: Player) {
        exempt.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onViolate(event: PlayerViolationEvent) {
        if (!exempt.contains(event.player.uniqueId)) {
            return
        }
        event.isCancelled = true
    }
}