package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.inventory.Inventory

object MenuHandler {
    private val menus = mutableMapOf<ExtendedInventory, EcoMenu>()
    private val inventories = mutableMapOf<Inventory, ExtendedInventory>()

    fun registerMenu(
        inventory: Inventory,
        menu: EcoMenu
    ) {
        val extendedInventory = ExtendedInventory(inventory, menu)
        inventories[inventory] = extendedInventory
        menus[extendedInventory] = menu
    }

    fun unregisterMenu(inventory: Inventory) {
        menus.remove(inventories[inventory])
        inventories.remove(inventory)
    }

    fun getMenu(inventory: Inventory): Menu? {
        return menus[inventories[inventory]]
    }

    fun getExtendedInventory(inventory: Inventory): ExtendedInventory? {
        return inventories[inventory]
    }
}