package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.NaturalExpGainEvent
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerExpChangeEvent

internal class NaturalExpGainBuilder(var reason: BuildReason) {
    private var cancelled = false
    var event: PlayerExpChangeEvent? = null
    var location: Location? = null

    fun push() {
        Validate.notNull(event)
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