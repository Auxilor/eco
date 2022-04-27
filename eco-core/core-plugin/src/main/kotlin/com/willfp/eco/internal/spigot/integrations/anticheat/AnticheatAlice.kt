package com.willfp.eco.internal.spigot.integrations.anticheat

import com.willfp.eco.core.integrations.anticheat.AnticheatIntegration
import me.nik.alice.api.events.AliceViolationEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.UUID

class AnticheatAlice : AnticheatIntegration, Listener {
    private val exempt: MutableSet<UUID> = HashSet()

    override fun getPluginName(): String {
        return "Alice"
    }

    override fun exempt(player: Player) {
        exempt.add(player.uniqueId)
    }

    override fun unexempt(player: Player) {
        exempt.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onViolate(event: AliceViolationEvent) {
        if (!exempt.contains(event.player.uniqueId)) {
            return
        }
        event.isCancelled = true
    }
}