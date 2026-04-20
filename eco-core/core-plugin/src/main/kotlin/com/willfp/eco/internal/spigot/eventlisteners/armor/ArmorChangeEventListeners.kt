package com.willfp.eco.internal.spigot.eventlisteners.armor

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.eco.core.events.ArmorEquipEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseArmorEvent

class ArmorChangeEventListeners(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun onArmorChange(event: ArmorEquipEvent) {
        val player = event.player
        val before = player.inventory.armorContents.toMutableList()
        plugin.scheduler.runTask(player) {
            val after = player.inventory.armorContents.toMutableList()
            val armorChangeEvent = ArmorChangeEvent(player, before, after)
            Bukkit.getPluginManager().callEvent(armorChangeEvent)
        }
    }

    @EventHandler
    fun dispenseArmorEvent(event: BlockDispenseArmorEvent) {
        val type = ArmorType.matchType(event.item)
        if (type != null && event.targetEntity is Player) {
            Bukkit.getPluginManager().callEvent(ArmorEquipEvent(event.targetEntity as Player))
        }
    }
}