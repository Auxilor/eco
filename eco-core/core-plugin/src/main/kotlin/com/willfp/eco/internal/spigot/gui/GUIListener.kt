package com.willfp.eco.internal.spigot.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.gui.slot.CustomSlot
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.menu.EcoMenu
import com.willfp.eco.internal.gui.menu.MenuHandler
import com.willfp.eco.internal.gui.menu.asRenderedInventory
import com.willfp.eco.internal.gui.menu.getMenu
import com.willfp.eco.internal.gui.slot.EcoSlot
import com.willfp.eco.util.MenuUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerItemHeldEvent

class GUIListener(private val plugin: EcoPlugin) : Listener {
    private fun Slot.handle(event: InventoryClickEvent, menu: EcoMenu) {
        when (this) {
            is EcoSlot -> this.handleInventoryClick(event, menu)
            is CustomSlot -> this.delegate.handle(event, menu)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleSlotClick(event: InventoryClickEvent) {
        val rendered = event.clickedInventory?.asRenderedInventory() ?: return

        val menu = rendered.menu

        val (row, column) = MenuUtils.convertSlotToRowColumn(event.slot)

        menu.getSlot(row, column).handle(event, menu)

        plugin.scheduler.run { rendered.render() }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleShiftClick(event: InventoryClickEvent) {
        if (!event.isShiftClick) {
            return
        }

        val player = event.whoClicked as? Player ?: return

        val inv = player.openInventory.topInventory

        if (inv == event.clickedInventory) {
            return
        }

        val menu = inv.getMenu() ?: return

        val (row, column) = MenuUtils.convertSlotToRowColumn(inv.firstEmpty())

        val slot = menu.getSlot(row, column)

        if (!slot.isCaptive) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleClose(event: InventoryCloseEvent) {
        val menu = event.inventory.getMenu() as? EcoMenu ?: return

        menu.handleClose(event)

        plugin.scheduler.run { MenuHandler.unregisterInventory(event.inventory) }
    }

    @EventHandler
    fun forceRender(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.renderActiveMenu()
    }

    @EventHandler
    fun forceRender(event: PlayerItemHeldEvent) {
        val player = event.player
        player.renderActiveMenu()
    }

    private fun Player.renderActiveMenu() {
        val inv = this.openInventory.topInventory

        val rendered = inv.asRenderedInventory() ?: return

        rendered.render()
        plugin.scheduler.run { rendered.render() }
    }
}
