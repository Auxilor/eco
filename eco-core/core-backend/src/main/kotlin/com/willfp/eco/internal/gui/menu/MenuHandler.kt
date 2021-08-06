package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.inventory.Inventory

object MenuHandler {
    private val MENUS: MutableMap<Inventory, Menu> = HashMap()

    fun registerMenu(
        inventory: Inventory,
        menu: Menu
    ) {
        MENUS[inventory] = menu
    }

    fun unregisterMenu(inventory: Inventory) {
        MENUS.remove(inventory)
    }

    fun getMenu(inventory: Inventory): Menu? {
        return MENUS[inventory]
    }
}