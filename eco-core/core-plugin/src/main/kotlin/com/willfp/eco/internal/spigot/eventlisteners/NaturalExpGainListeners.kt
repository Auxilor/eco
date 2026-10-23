package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.NaturalExpGainEvent
import java.util.concurrent.CopyOnWriteArrayList
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ThrownExpBottle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ExpBottleEvent
import org.bukkit.event.player.PlayerExpChangeEvent

class NaturalExpGainListenersPaper : Listener {
    @EventHandler
    fun onEvent(event: PlayerExpChangeEvent) {
        val source = event.source

        if (source is ThrownExpBottle) {
            return
        }

        val ecoEvent = NaturalExpGainEvent(event)
        Bukkit.getPluginManager().callEvent(ecoEvent)
    }
}

class NaturalExpGainListenersSpigot : Listener {
    private val bottles = CopyOnWriteArrayList<BrokenBottle>()

    @EventHandler
    fun playerChange(event: PlayerExpChangeEvent) {
        val now = System.currentTimeMillis()
        bottles.removeIf { now - it.time > BOTTLE_EXPIRY_MILLIS }

        val location = event.player.location
        val fromBottle = bottles.any {
            it.location.world == location.world && it.location.distanceSquared(location) <= BOTTLE_RADIUS_SQUARED
        }

        if (fromBottle) {
            return
        }

        Bukkit.getPluginManager().callEvent(NaturalExpGainEvent(event))
    }

    @EventHandler
    fun onExpBottle(event: ExpBottleEvent) {
        bottles.add(BrokenBottle(event.entity.location, System.currentTimeMillis()))
    }

    private data class BrokenBottle(val location: Location, val time: Long)

    private companion object {
        const val BOTTLE_RADIUS_SQUARED = 52.0
        const val BOTTLE_EXPIRY_MILLIS = 5000L
    }
}
