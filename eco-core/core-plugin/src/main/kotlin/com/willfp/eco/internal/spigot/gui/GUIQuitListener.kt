package com.willfp.eco.internal.spigot.gui

import com.willfp.eco.internal.gui.menu.removeForcedRenderedInventory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object GUIQuitListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleQuit(event: PlayerQuitEvent) {
        event.player.removeForcedRenderedInventory()
    }
}
