@file:JvmName("GUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuType
import com.willfp.eco.core.gui.menu.MenuEvent
import com.willfp.eco.core.gui.menu.MenuEventHandler
import com.willfp.eco.core.gui.page.Page
import com.willfp.eco.core.gui.page.PageBuilder
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.items.TestableItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

/** Shorthand for the `event.whoClicked as Player` pattern used everywhere. */
val InventoryClickEvent.player: Player
    get() = this.whoClicked as Player

/** @see SlotBuilder.onLeftClick */
fun SlotBuilder.onLeftClick(action: (event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onLeftClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onLeftClick */
fun SlotBuilder.onLeftClick(action: (Player, event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onLeftClick { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.onRightClick */
fun SlotBuilder.onRightClick(action: (event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onRightClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onRightClick */
fun SlotBuilder.onRightClick(action: (Player, event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onRightClick { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.onShiftLeftClick */
fun SlotBuilder.onShiftLeftClick(action: (event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftLeftClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onShiftLeftClick */
fun SlotBuilder.onShiftLeftClick(action: (Player, event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftLeftClick { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.onShiftRightClick */
fun SlotBuilder.onShiftRightClick(action: (event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftRightClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onShiftRightClick */
fun SlotBuilder.onShiftRightClick(action: (Player, event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onShiftRightClick { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.onMiddleClick */
fun SlotBuilder.onMiddleClick(action: (event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onMiddleClick { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onMiddleClick */
fun SlotBuilder.onMiddleClick(action: (Player, event: InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onMiddleClick { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.onClick */
fun SlotBuilder.onClick(clickType: ClickType, action: (InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onClick(clickType) { a, b, c -> action(a, b, c) }

/** @see SlotBuilder.onClick */
fun SlotBuilder.onClick(clickType: ClickType, action: (Player, InventoryClickEvent, Slot, Menu) -> Unit): SlotBuilder =
    this.onClick(clickType) { a, b, c -> action(a.player, a, b, c) }

/** @see SlotBuilder.notCaptiveFor */
fun SlotBuilder.notCaptiveFor(test: (Player) -> Boolean): SlotBuilder =
    this.notCaptiveFor { test(it) }

/**
 * @see SlotBuilder.setModifier
 * @deprecated Use SlotUpdater instead.
 */
@Deprecated("Use SlotUpdater instead")
@Suppress("DEPRECATION")
fun SlotBuilder.setModifier(action: (Player, Menu, item: ItemStack) -> Unit): SlotBuilder =
    this.setUpdater { a, b, c -> c.apply { action(a, b, c) } }

/** @see SlotBuilder.setUpdater */
fun SlotBuilder.setUpdater(action: (Player, Menu, item: ItemStack) -> ItemStack): SlotBuilder =
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
fun MenuBuilder.onClose(action: (event: InventoryCloseEvent, Menu) -> Unit): MenuBuilder =
    this.onClose { a, b -> action(a, b) }

/** @see MenuBuilder.onOpen */
fun MenuBuilder.onOpen(action: (Player, Menu) -> Unit): MenuBuilder =
    this.onOpen { a, b -> action(a, b) }

/** @see MenuBuilder.modify */
fun MenuBuilder.modify(modifier: (builder: MenuBuilder) -> Unit): MenuBuilder =
    this.modfiy { modifier(it) }

/** @see MenuBuilder.onRender */
fun MenuBuilder.onRender(action: (Player, Menu) -> Unit): MenuBuilder =
    this.onRender { a, b -> action(a, b) }

/** @see MenuBuilder.addPage */
fun MenuBuilder.addPage(page: Int, creation: PageBuilder.() -> Unit): MenuBuilder {
    val builder = Menu.builder(this.rows)
    creation(builder)
    return this.addPage(Page(page, builder.build()))
}

/** @see MenuBuilder.onEvent */
inline fun <reified T : MenuEvent> MenuBuilder.onEvent(crossinline handler: (Player, Menu, event: T) -> Unit): MenuBuilder {
    return this.onEvent(object : MenuEventHandler<T>(T::class.java) {
        override fun handle(player: Player, menu: Menu, event: T) =
            handler(player, menu, event)
    })
}

/** Kotlin builder for menus. */
fun menu(
    rows: Int,
    init: MenuBuilder.() -> Unit
): Menu {
    val builder = Menu.builder(rows)
    init(builder)
    return builder.build()
}

/** Kotlin builder for menus. */
fun dispenserMenu(
    init: MenuBuilder.() -> Unit
): Menu {
    val builder = Menu.builder(MenuType.DISPENSER)
    init(builder)
    return builder.build()
}
