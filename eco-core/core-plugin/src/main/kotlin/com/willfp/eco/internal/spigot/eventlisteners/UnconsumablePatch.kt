package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.internal.items.ArgParserUnconsumable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.persistence.PersistentDataType

object UnconsumablePatch : Listener {
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val meta = event.item.itemMeta ?: return
        if (!meta.persistentDataContainer.has(ArgParserUnconsumable.key, PersistentDataType.BOOLEAN)) return
        event.isCancelled = true
    }
}
