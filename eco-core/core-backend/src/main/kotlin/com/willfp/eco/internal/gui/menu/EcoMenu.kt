package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.slot.EcoSlot
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Suppress("UNCHECKED_CAST")
class EcoMenu(
    private val rows: Int,
    val slots: List<MutableList<EcoSlot>>,
    private val title: String,
    private val onClose: CloseHandler
) : Menu {
    override fun getSlot(row: Int, column: Int): Slot {
        if (row < 1 || row > this.rows) {
            return slots[0][0]
        }

        if (column < 1 || column > 9) {
            return slots[0][0]
        }

        return slots[row - 1][column - 1]
    }

    override fun open(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, rows * 9, title)

        var i = 0
        for (row in slots) {
            for (slot in row) {
                if (i == rows * 9) {
                    break
                }
                inventory.setItem(i, slot.getItemStack(player, this))
                i++
            }
        }

        player.openInventory(inventory)
        MenuHandler.registerInventory(inventory, this, player)
        return inventory
    }

    fun handleClose(event: InventoryCloseEvent) {
        onClose.handle(event, this)
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

    override fun <T : Any, Z : Any> writeData(
        player: Player,
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        value: Z
    ) {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return
        inventory.data[key] = value
        inventory.render()
    }

    override fun <T : Any, Z : Any> readData(player: Player, key: NamespacedKey, type: PersistentDataType<T, Z>): T? {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return null
        return inventory.data[key] as? T?
    }

    override fun getKeys(player: Player): Set<NamespacedKey> {
        val inventory = player.openInventory.topInventory.asRenderedInventory() ?: return emptySet()
        return inventory.data.keys
    }

    override fun refresh(player: Player) {
        player.openInventory.topInventory.asRenderedInventory()?.render()
    }
}
