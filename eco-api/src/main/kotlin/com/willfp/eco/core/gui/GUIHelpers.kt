@file:JvmName("GUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.items.TestableItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

/** @see SlotBuilder.onLeftClick */
fun SlotBuilder.onLeftClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onLeftClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onRightClick */
fun SlotBuilder.onRightClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onRightClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onShiftLeftClick */
fun SlotBuilder.onShiftLeftClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftLeftClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onShiftRightClick */
fun SlotBuilder.onShiftRightClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftRightClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onShiftRightClick */
fun SlotBuilder.onMiddleClick(action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onMiddleClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.notCaptiveFor */
fun SlotBuilder.notCaptiveFor(test: (Player) -> Boolean): SlotBuilder =
    this.notCaptiveFor { test(it) }

/**
 * @see SlotBuilder.setModifier
 * @deprecated Use SlotUpdater instead.
 */
@Deprecated("Use SlotUpdater instead")
@Suppress("DEPRECATION")
fun SlotBuilder.setModifier(action: (Player, Menu, ItemStack) -> Unit): SlotBuilder =
    this.setUpdater { a, b, c -> c.apply { action(a, b, c) } }

/** @see SlotBuilder.setUpdater */
fun SlotBuilder.setUpdater(action: (Player, Menu, ItemStack) -> ItemStack): SlotBuilder =
    this.setUpdater { a, b, c -> action(a, b, c) }

/** Kotlin builder for slots. */
fun captiveSlot(): Slot = Slot.builder().setCaptive().build()

/** Kotlin builder for slots. */
fun captiveSlot(
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder().setCaptive()
    init(builder)
    return builder.build()
}

/** Kotlin builder for slots. */
fun slot(
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder()
    init(builder)
    return builder.build()
}

/** Kotlin builder for slots. */
fun slot(
    item: ItemStack,
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder(item)
    init(builder)
    return builder.build()
}

/** Kotlin builder for slots. */
fun slot(
    item: ItemStack
): Slot = Slot.builder(item).build()

/** Kotlin builder for slots. */
fun slot(
    item: TestableItem,
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder(item)
    init(builder)
    return builder.build()
}

/** Kotlin builder for slots. */
fun slot(
    item: TestableItem
): Slot = Slot.builder(item.item).build()

/** Kotlin builder for slots. */
fun slot(
    provider: (Player, Menu) -> ItemStack,
    init: SlotBuilder.() -> Unit
): Slot {
    val builder = Slot.builder { a, b -> provider(a, b) }
    init(builder)
    return builder.build()
}

/** Kotlin builder for slots. */
fun slot(
    provider: (Player, Menu) -> ItemStack
): Slot = Slot.builder { a, b -> provider(a, b) }.build()

/** @see MenuBuilder.onClose */
fun MenuBuilder.onClose(action: (InventoryCloseEvent, Menu) -> Unit): MenuBuilder =
    this.onClose { a, b -> action(a, b) }

/** @see MenuBuilder.onOpen */
fun MenuBuilder.onOpen(action: (Player, Menu) -> Unit): MenuBuilder =
    this.onOpen { a, b -> action(a, b) }

/** @see MenuBuilder.modify */
fun MenuBuilder.modify(modifier: (MenuBuilder) -> Unit): MenuBuilder =
    this.modfiy { modifier(it) }

/** @see MenuBuilder.onRender */
fun MenuBuilder.onRender(action: (Player, Menu) -> Unit): MenuBuilder =
    this.onRender { a, b -> action(a, b) }

/** Kotlin builder for menus. */
fun menu(
    rows: Int,
    init: MenuBuilder.() -> Unit
): Menu {
    val builder = Menu.builder(rows)
    init(builder)
    return builder.build()
}
