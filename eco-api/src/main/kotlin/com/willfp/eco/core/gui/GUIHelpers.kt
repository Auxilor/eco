@file:JvmName("GUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuEvent
import com.willfp.eco.core.gui.menu.MenuEventHandler
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.menu.MenuType
import com.willfp.eco.core.gui.page.Page
import com.willfp.eco.core.gui.page.PageBuilder
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.sound.PlayableSound
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

/** @see SlotBuilder.setCaptiveFilter */
fun SlotBuilder.setCaptiveFilter(test: (Player, Menu, ItemStack?) -> Boolean): SlotBuilder =
    this.setCaptiveFilter { a, b, c -> test(a, b, c) }

/**
 * @see SlotBuilder.setModifier
 * @deprecated Use SlotUpdater instead.
 */
@Deprecated("Use SlotUpdater instead")
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

/** @see MenuBuilder.onBuild */
fun MenuBuilder.onBuild(action: (Menu) -> Unit): MenuBuilder =
    this.onBuild { action(it) }

/**
 * Add a page changer button placed directly from a config section.
 *
 * Reads the combined-lookup shape: `basePath.item` for the active button and the
 * optional `basePath.item-inactive` for the first or last page. The location is
 * read tolerantly as `basePath.location.row` / `basePath.location.column`,
 * falling back to a flat `basePath.row` / `basePath.column`. If no active item is
 * configured the changer is not added, so a missing button stays hidden rather
 * than rendering a broken item.
 *
 * @param config The config holding the button section.
 * @param basePath The path of the button section.
 * @param direction The direction.
 * @param sound The page turn sound, or null for silent.
 * @param layer The layer, defaults to [MenuLayer.TOP].
 * @return The builder.
 */
fun MenuBuilder.addPageChanger(
    config: Config,
    basePath: String,
    direction: PageChanger.Direction,
    sound: PlayableSound?,
    layer: Int = MenuLayer.TOP
): MenuBuilder {
    val active = config.getStringOrNull("$basePath.item")
        ?.let { Items.lookup(it).item }
        ?: return this

    val inactive = config.getStringOrNull("$basePath.item-inactive")
        ?.let { Items.lookup(it).item }

    val row = config.getIntOrNull("$basePath.location.row") ?: config.getInt("$basePath.row")
    val column = config.getIntOrNull("$basePath.location.column") ?: config.getInt("$basePath.column")

    return addPageChanger(direction, active, inactive, sound, row, column, layer)
}

/**
 * Add a page changer button from prebuilt items.
 *
 * Use this when the button items are built by the plugin (for example a split
 * active and inactive config shape with separate name and lore). The behaviour
 * matches the config overload: a null inactive item hides the button on the
 * first or last page.
 *
 * @param direction The direction.
 * @param active The active item.
 * @param inactive The inactive item, or null to hide on the first or last page.
 * @param sound The page turn sound, or null for silent.
 * @param row The row.
 * @param column The column.
 * @param layer The layer, defaults to [MenuLayer.TOP].
 * @return The builder.
 */
fun MenuBuilder.addPageChanger(
    direction: PageChanger.Direction,
    active: ItemStack,
    inactive: ItemStack?,
    sound: PlayableSound?,
    row: Int,
    column: Int,
    layer: Int = MenuLayer.TOP
): MenuBuilder {
    val changer = PageChanger.builder(direction)
        .active(active)
        .inactive(inactive)
        .sound(sound)
        .build()

    return addComponent(layer, row, column, changer)
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
