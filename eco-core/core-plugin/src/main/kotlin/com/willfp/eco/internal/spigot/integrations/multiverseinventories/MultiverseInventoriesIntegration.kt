package com.willfp.eco.internal.spigot.integrations.multiverseinventories

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.ArmorChangeEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.mvplugins.multiverse.inventories.event.WorldChangeShareHandlingEvent

class MultiverseInventoriesIntegration(
    private val plugin: EcoPlugin
): Listener {
    @EventHandler
    fun onWorldChange(event: WorldChangeShareHandlingEvent) {
        val before = event.player.inventory.armorContents.toMutableList()
        this.plugin.scheduler.run {
            val after = event.player.inventory.armorContents.toMutableList()
            Bukkit.getPluginManager().callEvent(ArmorChangeEvent(event.player, before, after))
        }
    }
}