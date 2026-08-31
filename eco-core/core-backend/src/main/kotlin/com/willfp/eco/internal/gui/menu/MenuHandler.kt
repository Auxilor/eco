package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import java.util.Collections
import java.util.WeakHashMap
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

private val inventories: MutableMap<Inventory, RenderedInventory> =
    Collections.synchronizedMap(WeakHashMap())

object MenuHandler {
    fun registerInventory(
        inventory: Inventory,
        menu: EcoMenu,
        player: Player
    ): RenderedInventory {
        val rendered = RenderedInventory(menu, inventory, player)
        inventories[inventory] = rendered
        return rendered
    }

    fun unregisterInventory(inventory: Inventory) {
        inventories.remove(inventory)
    }
}

fun Inventory.asRenderedInventory(): RenderedInventory? =
    inventories[this]

fun Inventory.getMenu(): Menu? =
    this.asRenderedInventory()?.menu
