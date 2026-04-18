package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.NaturalExpGainEvent
import org.bukkit.Bukkit
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
    private val events: MutableSet<NaturalExpGainBuilder> = HashSet()

    @EventHandler
    fun playerChange(event: PlayerExpChangeEvent) {
        val builder = NaturalExpGainBuilder(NaturalExpGainBuilder.BuildReason.PLAYER)
        builder.event = event
        var toRemove: NaturalExpGainBuilder? = null
        for (searchBuilder in events) {
            if (searchBuilder.location!!.world != event.player.location.world) {
                continue
            }
            if (searchBuilder.reason == NaturalExpGainBuilder.BuildReason.BOTTLE && searchBuilder.location!!.distanceSquared(
                    event.player.location
                ) > 52
            ) {
                toRemove = searchBuilder
            }
        }
        if (toRemove != null) {
            events.remove(toRemove)
            return
        }
        builder.event = event
        builder.push()
        events.remove(builder)
    }

    @EventHandler
    fun onExpBottle(event: ExpBottleEvent) {
        val builtEvent = NaturalExpGainBuilder(NaturalExpGainBuilder.BuildReason.BOTTLE)
        builtEvent.location = event.entity.location
        events.add(builtEvent)
    }
}