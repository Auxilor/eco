package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.inventory.Inventory

object MenuHandler {
    private val MENUS: MutableMap<ExtendedInventory, EcoMenu> = HashMap()
    private val INVS: MutableMap<Inventory, ExtendedInventory> = HashMap()

    fun registerMenu(
        inventory: Inventory,
        menu: EcoMenu
    ) {
        val extendedInventory = ExtendedInventory(inventory, menu)
        INVS[inventory] = extendedInventory
        MENUS[extendedInventory] = menu
    }

    fun unregisterMenu(inventory: Inventory) {
        MENUS.remove(INVS[inventory])
        INVS.remove(inventory)
    }

    fun getMenu(inventory: Inventory): Menu? {
        return MENUS[INVS[inventory]]
    }

    fun getExtendedInventory(inventory: Inventory): ExtendedInventory? {
        return INVS[inventory]
    }
}