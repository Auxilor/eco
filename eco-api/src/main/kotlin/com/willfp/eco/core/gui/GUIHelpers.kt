@file:JvmName("GUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

/**
 * @see SlotBuilder.onLeftClick
 */
fun SlotBuilder.onLeftClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onLeftClick { a, b, c -> action(a, b, c) }

/**
 * @see SlotBuilder.onRightClick
 */
fun SlotBuilder.onRightClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onRightClick { a, b, c -> action(a, b, c) }

/**
 * @see SlotBuilder.onShiftLeftClick
 */
fun SlotBuilder.onShiftLeftClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftLeftClick { a, b, c -> action(a, b, c) }

/**
 * @see SlotBuilder.onShiftRightClick
 */
fun SlotBuilder.onShiftRightClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftRightClick { a, b, c -> action(a, b, c) }

/**
 * @see SlotBuilder.onShiftRightClick
 */
fun SlotBuilder.onMiddleClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onMiddleClick { a, b, c -> action(a, b, c) }

/**
 * @see SlotBuilder.setModifier
 */
fun SlotBuilder.setModifier(action: (Player, Menu, ItemStack) -> Unit): SlotBuilder =
    this.setModifier { a, b, c -> action(a, b, c) }

/**
 * Kotlin builder for slots.
 */
fun slot(
    item: ItemStack,
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder(item)
    init(builder)
    return builder.build()
}

/**
 * Kotlin builder for slots.
 */
fun slot(
    provider: (Player, Menu) -> ItemStack,
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder { a, b -> provider(a, b) }
    init(builder)
    return builder.build()
}

/**
 * @see MenuBuilder.onClose
 */
fun MenuBuilder.onClose(action: (InventoryCloseEvent, Menu) -> Unit): MenuBuilder =
    this.onClose { a, b -> action(a, b) }

/**
 * @see MenuBuilder.modify
 */
fun MenuBuilder.modify(modifier: (MenuBuilder) -> Unit): MenuBuilder =
    this.modfiy { modifier(it) }

/**
 * Kotlin builder for menus.
 */
fun menu(
    rows: Int,
    init: MenuBuilder.() -> Unit
): Menu {
    val builder = Menu.builder(rows)
    init(builder)
    return builder.build()
}
