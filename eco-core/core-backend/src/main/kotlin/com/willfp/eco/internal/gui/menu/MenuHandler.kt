package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.WeakHashMap

private val inventories = WeakHashMap<Inventory, MenuRenderedInventory>()

object MenuHandler {
    fun registerInventory(
        inventory: Inventory,
        menu: EcoMenu,
        player: Player
    ) {
        val rendered = MenuRenderedInventory(menu, inventory, player)
        inventories[inventory] = rendered
    }

    fun unregisterInventory(inventory: Inventory) {
        inventories.remove(inventory)
    }
}

fun Inventory.asRenderedInventory(): MenuRenderedInventory? =
    inventories[this]

fun Inventory.getMenu(): Menu? =
    this.asRenderedInventory()?.menu
