package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.slot.EcoSlot
import com.willfp.eco.util.StringUtils
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
): Menu {
    override fun getSlot(row: Int, column: Int): Slot {
        if (row < 1 || row > this.rows) {
            throw IllegalArgumentException("Invalid row number!")
        }

        if (column < 1 || column > 9) {
            throw IllegalArgumentException("Invalid column number!")
        }

        return slots[row - 1][column - 1]
    }

    override fun open(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, rows * 9, title)

        var i = 0
        for (row in slots) {
            for (item in row) {
                if (i == rows * 9) {
                    break
                }
                val slotItem = item.getItemStack(player, this)
                val meta = slotItem.itemMeta
                if (meta != null) {
                    val lore = meta.lore
                    if (lore != null) {
                        lore.replaceAll{ s -> StringUtils.format(s, player) }
                        meta.lore = lore
                    }
                    slotItem.itemMeta = meta
                }
                inventory.setItem(i, slotItem)
                i++
            }
        }

        player.openInventory(inventory)
        MenuHandler.registerMenu(inventory, this)
        return inventory
    }

    fun handleClose(event: InventoryCloseEvent) {
        onClose.handle(event, this)
    }

    override fun getRows(): Int {
        return rows
    }

    override fun getTitle(): String {
        return title
    }

    override fun getCaptiveItems(player: Player): MutableList<ItemStack> {
        val inventory = MenuHandler.getExtendedInventory(player.openInventory.topInventory)
        inventory ?: return mutableListOf()
        return inventory.captiveItems
    }

    override fun <T : Any, Z : Any> writeData(
        player: Player,
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        value: Z
    ) {
        val inventory = MenuHandler.getExtendedInventory(player.openInventory.topInventory)
        inventory ?: return
        inventory.data[key] = value
        inventory.refresh(player)
    }

    override fun <T : Any, Z : Any> readData(player: Player, key: NamespacedKey, type: PersistentDataType<T, Z>): T? {
        val inventory = MenuHandler.getExtendedInventory(player.openInventory.topInventory)
        inventory ?: return null
        return inventory.data[key] as T?
    }

    override fun getKeys(player: Player): MutableSet<NamespacedKey> {
        val inventory = MenuHandler.getExtendedInventory(player.openInventory.topInventory)
        inventory ?: return HashSet()
        return inventory.data.keys
    }
}