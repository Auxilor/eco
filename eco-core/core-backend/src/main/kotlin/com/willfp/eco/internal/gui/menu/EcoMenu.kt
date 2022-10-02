package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuEvent
import com.willfp.eco.core.gui.menu.MenuEventHandler
import com.willfp.eco.core.gui.menu.OpenHandler
import com.willfp.eco.core.gui.slot.FillerSlot
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Suppress("UNCHECKED_CAST")
class EcoMenu(
    private val rows: Int,
    private val columns: Int,
    private val componentsAtPoints: Map<GUIPosition, List<OffsetComponent>>,
    private val title: String,
    private val onClose: List<CloseHandler>,
    private val onRender: List<(Player, Menu) -> Unit>,
    private val onOpen: List<OpenHandler>,
    private val menuEventHandlers: List<MenuEventHandler<*>>,
    private val allowsChangingHeldItem: Boolean
) : Menu {
    private fun getPossiblyReactiveSlot(row: Int, column: Int, player: Player?, menu: Menu?): Slot {
        if (row < 1 || row > this.rows || column < 1 || column > this.columns) {
            return emptyFillerSlot
        }

        val guiPosition = GUIPosition(row, column)
        val components = componentsAtPoints[guiPosition] ?: return emptyFillerSlot

        for (component in components) {
            val found = if (player != null && menu != null) component.component.getSlotAt(
                component.rowOffset,
                component.columnOffset,
                player,
                menu
            ) else component.component.getSlotAt(
                component.rowOffset,
                component.columnOffset
            )

            if (found != null) {
                return found
            }
        }

        return emptyFillerSlot
    }

    override fun getSlot(row: Int, column: Int): Slot =
        getPossiblyReactiveSlot(row, column, null, null)

    override fun getSlot(row: Int, column: Int, player: Player, menu: Menu): Slot =
        getPossiblyReactiveSlot(row, column, player, menu)

    override fun open(player: Player): Inventory {
        val inventory = if (columns == 9) {
            Bukkit.createInventory(null, rows * columns, title)
        } else {
            Bukkit.createInventory(null, InventoryType.DISPENSER, title)
        }

        // Register the inventory first
        val rendered = MenuHandler.registerInventory(inventory, this, player)

        // Set the player's open menu forcefully
        player.forceRenderedInventory(rendered)

        // Render default captive items
        rendered.renderDefaultCaptiveItems()

        // Run onOpen with items already there
        onOpen.forEach { it.handle(player, this) }

        // Run second render with updated state from onOpen
        rendered.render()

        // Show the inventory to the player
        player.openInventory(inventory)

        // Stop forcing the menu to be open, as Player#openInventory#topInventory is now the menu
        player.removeForcedRenderedInventory()

        return inventory
    }

    fun handleClose(event: InventoryCloseEvent) {
        onClose.forEach { it.handle(event, this) }
        MenuHandler.unregisterInventory(event.inventory)
    }

    override fun getRows() = rows

    override fun getColumns() = columns

    override fun getTitle() = title

    override fun getCaptiveItems(player: Player): List<ItemStack> {
        val inventory = player.renderedInventory ?: return emptyList()
        return inventory.captiveItems.values.toList()
    }

    override fun getCaptiveItem(player: Player, row: Int, column: Int): ItemStack? {
        if (row < 1 || row > this.rows || column < 1 || column > this.columns) {
            return null
        }

        val inventory = player.renderedInventory ?: return null
        return inventory.captiveItems[GUIPosition(row, column)]
    }

    override fun callEvent(player: Player, event: MenuEvent) {
        for (handler in menuEventHandlers) {
            if (handler.canHandleEvent(event)) {
                handler.handle(event, player)
            }
        }
    }

    private fun <T : MenuEvent> MenuEventHandler<T>.handle(event: MenuEvent, player: Player) {
        this.handle(player, this@EcoMenu, event as T)
    }

    override fun addState(player: Player, key: String, value: Any?) {
        val inventory = player.renderedInventory ?: return
        inventory.state[key] = value
    }

    override fun getState(player: Player): Map<String, Any?> {
        val inventory = player.renderedInventory ?: return emptyMap()
        return inventory.state.toMap()
    }

    override fun <T : Any> getState(player: Player, key: String): T? {
        val inventory = player.renderedInventory ?: return null
        return inventory.state[key] as? T?
    }

    override fun removeState(player: Player, key: String) {
        val inventory = player.renderedInventory ?: return
        inventory.state.remove(key)
    }

    override fun clearState(player: Player) {
        val inventory = player.renderedInventory ?: return
        inventory.state.clear()
    }

    override fun refresh(player: Player) {
        player.renderedInventory?.render()
    }

    override fun allowsChangingHeldItem(): Boolean {
        return this.allowsChangingHeldItem
    }

    fun runOnRender(player: Player) =
        onRender.forEach { it(player, this) }
}

data class OffsetComponent(
    val component: GUIComponent,
    val rowOffset: Int,
    val columnOffset: Int
)

data class GUIPosition(
    val row: Int,
    val column: Int
)

val emptyFillerSlot = FillerSlot(ItemStack(Material.AIR))
