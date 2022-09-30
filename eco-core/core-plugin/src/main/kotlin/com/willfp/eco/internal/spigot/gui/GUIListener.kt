package com.willfp.eco.internal.spigot.gui

import com.willfp.eco.core.EcoPlugin
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
    // Prevents StackOverflow exceptions with poorly implemented custom slots.
    private val depthLimit = 32

    private fun Slot.handle(
        player: Player,
        event: InventoryClickEvent,
        menu: EcoMenu,
        depth: Int = 0
    ) {
        // Always cancel on first depth to prevent bugs with custom slot implementations.
        if (depth == 0) {
            event.isCancelled = true
        }

        if (depth >= depthLimit) {
            return
        }

        val delegate = this.getActionableSlot(player, menu)

        if (delegate is EcoSlot) {
            delegate.handleInventoryClick(event, menu)
        } else if (delegate === this) {
            return
        } else {
            delegate.handle(player, event, menu, depth + 1)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleSlotClick(event: InventoryClickEvent) {
        val rendered = event.clickedInventory?.asRenderedInventory() ?: return

        val player = event.whoClicked as? Player ?: return

        val menu = rendered.menu

        val (row, column) = MenuUtils.convertSlotToRowColumn(event.slot, menu.columns)

        menu.getSlot(row, column, player, menu).handle(player, event, menu)

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

        val (row, column) = MenuUtils.convertSlotToRowColumn(inv.firstEmpty(), menu.columns)

        val slot = menu.getSlot(row, column, player, menu)

        if (!slot.isCaptive(player, menu)) {
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
