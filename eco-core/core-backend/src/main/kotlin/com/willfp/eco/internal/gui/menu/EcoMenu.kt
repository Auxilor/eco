package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.component.GUIComponent
import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.OpenHandler
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.slot.EcoFillerSlot
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Suppress("UNCHECKED_CAST")
class EcoMenu(
    private val rows: Int,
    private val componentsAtPoints: Map<Anchor, OffsetComponent>,
    private val title: String,
    private val onClose: CloseHandler,
    private val onRender: (Player, Menu) -> Unit,
    private val onOpen: OpenHandler
) : Menu {
    private fun getComponent(row: Int, column: Int): OffsetComponent? {
        if (row < 1 || row > this.rows || column < 1 || column > 9) {
            return emptyOffsetComponent
        }

        return componentsAtPoints[Anchor(row, column)]
    }

    override fun getSlot(row: Int, column: Int): Slot {
        val found = getComponent(row, column) ?: return emptyFillerSlot

        return found.component.getSlotAt(
            found.rowOffset,
            found.columnOffset
        ) ?: emptyFillerSlot
    }

    override fun getSlot(row: Int, column: Int, player: Player, menu: Menu): Slot {
        val found = getComponent(row, column) ?: return emptyFillerSlot

        return found.component.getSlotAt(
            found.rowOffset,
            found.columnOffset,
            player,
            menu
        ) ?: emptyFillerSlot
    }

    override fun open(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, rows * 9, title)

        inventory.asRenderedInventory()?.render(noSideEffects = true)

        player.openInventory(inventory)

        onOpen.handle(player, this)

        inventory.asRenderedInventory()?.generateCaptive()
        return inventory
    }

    fun handleClose(event: InventoryCloseEvent) {
        onClose.handle(event, this)
        event.inventory.asRenderedInventory()?.generateCaptive()
        MenuHandler.unregisterInventory(event.inventory)
    }

    override fun getRows(): Int {
        return rows
    }

    override fun getTitle(): String {
        return title
    }

    override fun getCaptiveItems(player: Player): List<ItemStack> {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return emptyList()
        return inventory.captiveItems
    }

    @Deprecated("Deprecated in Java", ReplaceWith("addState(player, key.toString(), value)"))
    override fun <T : Any, Z : Any> writeData(
        player: Player,
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        value: Z
    ) = addState(player, key.toString(), value)

    @Deprecated("Deprecated in Java", ReplaceWith("getState(player, key.toString())"))
    override fun <T : Any, Z : Any> readData(player: Player, key: NamespacedKey, type: PersistentDataType<T, Z>): T? =
        getState(player, key.toString())

    @Deprecated("Deprecated in Java")
    override fun getKeys(player: Player): Set<NamespacedKey> {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return emptySet()
        return inventory.state.keys.mapNotNull { NamespacedKeyUtils.fromStringOrNull(it) }.toSet()
    }

    override fun addState(player: Player, key: String, value: Any?) {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return
        inventory.state[key] = value
    }

    override fun getState(player: Player): Map<String, Any?> {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return emptyMap()
        return inventory.state.toMap()
    }

    override fun <T : Any> getState(player: Player, key: String): T? {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return null
        return inventory.state[key] as? T?
    }

    override fun removeState(player: Player, key: String) {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return
        inventory.state.remove(key)
    }

    override fun clearState(player: Player) {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return
        inventory.state.clear()
    }

    override fun refresh(player: Player) {
        player.openInventory.topInventory.asRenderedInventory()?.render()
    }

    fun runOnRender(player: Player) = onRender(player, this)
}

data class OffsetComponent(
    val component: GUIComponent,
    val rowOffset: Int,
    val columnOffset: Int
)

data class Anchor(
    val row: Int,
    val column: Int
)

val emptyFillerSlot = EcoFillerSlot(ItemStack(Material.AIR))
val emptyOffsetComponent = OffsetComponent(emptyFillerSlot, 0, 0)
