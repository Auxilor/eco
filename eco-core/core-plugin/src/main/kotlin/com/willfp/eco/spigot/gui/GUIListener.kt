package com.willfp.eco.spigot.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.internal.gui.menu.EcoMenu
import com.willfp.eco.internal.gui.menu.MenuHandler
import com.willfp.eco.internal.gui.slot.EcoSlot
import com.willfp.eco.util.MenuUtils
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUIListener(plugin: EcoPlugin) : PluginDependent<EcoPlugin>(plugin), Listener {
    @EventHandler
    fun handleSlotClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player !is Player) {
            return
        }
        val menu = MenuHandler.getMenu(event.clickedInventory ?: return) ?: return
        val rowColumn = MenuUtils.convertSlotToRowColumn(event.slot)
        val row = rowColumn.first!!
        val column = rowColumn.second!!
        val slot = menu.getSlot(row, column)
        Validate.isTrue(slot is EcoSlot, "Slot not instance of EcoSlot!")
        val ecoSlot = menu.getSlot(row, column) as EcoSlot
        event.isCancelled = true
        ecoSlot.handleInventoryClick(event, menu)

        plugin.scheduler.run{ MenuHandler.getExtendedInventory(event.clickedInventory!!).refresh(player) }
    }

    @EventHandler
    fun handleCaptivatorSlots(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player !is Player) {
            return
        }

        val menu = MenuHandler.getMenu(player.openInventory.topInventory) ?: return

        plugin.scheduler.run{ MenuHandler.getExtendedInventory(player.openInventory.topInventory).refresh(player) }
        plugin.scheduler.runLater({ Bukkit.getLogger().info(menu.getCaptiveItems(player).toString()) }, 1)
    }

    @EventHandler
    fun handleShiftClick(event: InventoryClickEvent) {
        if (!(event.click == ClickType.SHIFT_RIGHT || event.click == ClickType.SHIFT_LEFT)) {
            return
        }

        val player = event.whoClicked
        if (player !is Player) {
            return
        }

        val inv = player.openInventory.topInventory

        val menu = MenuHandler.getMenu(inv) ?: return

        val rowColumn = MenuUtils.convertSlotToRowColumn(inv.firstEmpty())
        val row = rowColumn.first!!
        val column = rowColumn.second!!
        val slot = menu.getSlot(row, column)

        if (!slot.isCaptive) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleClose(event: InventoryCloseEvent) {
        val player = event.player
        if (player !is Player) {
            return
        }
        val menu = MenuHandler.getMenu(event.inventory) ?: return
        Validate.isTrue(menu is EcoMenu, "Menu not instance of EcoMenu!")
        val ecoMenu = menu as EcoMenu
        ecoMenu.handleClose(event)

        DropQueue(player)
            .addItems(ecoMenu.getCaptiveItems(player))
            .setLocation(player.location)
            .forceTelekinesis()
            .push()

        plugin.scheduler.run { MenuHandler.unregisterMenu(event.inventory) }
    }
}