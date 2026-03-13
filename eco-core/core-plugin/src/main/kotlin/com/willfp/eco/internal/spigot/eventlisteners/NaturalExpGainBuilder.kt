package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.NaturalExpGainEvent
import moss.factions.shade.com.google.common.base.Preconditions
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerExpChangeEvent

internal class NaturalExpGainBuilder(var reason: BuildReason) {
    private var cancelled = false
    var event: PlayerExpChangeEvent? = null
    var location: Location? = null

    fun push() {
        Preconditions.checkNotNull(event, "PlayerExpChangeEvent cannot be null!")
        if (cancelled) {
            return
        }
        val naturalExpGainEvent = NaturalExpGainEvent(event!!)
        Bukkit.getPluginManager().callEvent(naturalExpGainEvent)
    }

    enum class BuildReason {
        BOTTLE, PLAYER
    }
}