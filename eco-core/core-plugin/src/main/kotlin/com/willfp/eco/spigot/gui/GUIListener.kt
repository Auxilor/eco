package com.willfp.eco.spigot.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.internal.gui.menu.EcoMenu
import com.willfp.eco.internal.gui.menu.MenuHandler
import com.willfp.eco.internal.gui.slot.EcoSlot
import org.apache.commons.lang.Validate
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUIListener(plugin: EcoPlugin) : PluginDependent<EcoPlugin>(plugin), Listener {
    @EventHandler
    fun handleSlotClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) {
            return
        }
        if (event.clickedInventory == null) {
            return
        }
        val menu = MenuHandler.getMenu(event.clickedInventory!!) ?: return
        val row = Math.floorDiv(event.slot, 9)
        val column = event.slot - row * 9
        val slot = menu.getSlot(row, column)
        Validate.isTrue(slot is EcoSlot, "Slot not instance of EcoSlot!")
        val ecoSlot = menu.getSlot(row, column) as EcoSlot
        event.isCancelled = true
        ecoSlot.handleInventoryClick(event)
    }

    @EventHandler
    fun handleClose(event: InventoryCloseEvent) {
        if (event.player !is Player) {
            return
        }
        val menu = MenuHandler.getMenu(event.inventory) ?: return
        Validate.isTrue(menu is EcoMenu, "Menu not instance of EcoMenu!")
        val ecoMenu = menu as EcoMenu
        ecoMenu.handleClose(event)
        plugin.scheduler.run { MenuHandler.unregisterMenu(event.inventory) }
    }
}