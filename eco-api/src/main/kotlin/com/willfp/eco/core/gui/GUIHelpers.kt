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
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.sound.AbstractPlayableSound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus

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

/** Kotlin builder for captive slots. */
fun captiveSlot(): Slot = Slot.builder().setCaptive().build()

/** Kotlin builder for captive slots. */
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

/**
 * Run a function to modify the builder.
 *
 * @see MenuBuilder.modfiy
 */
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
 * A row and column in a menu.
 *
 * @property row The row.
 * @property column The column.
 */
data class SlotLocation(
    val row: Int,
    val column: Int
)

/**
 * Get the slots a config section places something in.
 *
 * Reads `path.locations`, a list of `row` / `column` sections, so one item can
 * fill several slots. Without it, reads `path.location.row` / `path.location.column`,
 * falling back to a flat `path.row` / `path.column`.
 *
 * @param path The path of the section, or empty for this config itself.
 * @param default The locations to return when none of those keys are set, or null to read the
 * flat row and column regardless.
 * @return The slot locations, never empty.
 */
fun Config.getSlotLocations(path: String = "", default: List<SlotLocation>? = null): List<SlotLocation> {
    val prefix = if (path.isEmpty()) "" else "$path."

    val locations = getSubsections("${prefix}locations")

    return when {
        locations.isNotEmpty() -> locations.map { SlotLocation(row = it.getInt("row"), column = it.getInt("column")) }
        default != null && !has("${prefix}location.row") && !has("${prefix}row") -> default
        else -> listOf(
            SlotLocation(
                row = getIntOrNull("${prefix}location.row") ?: getInt("${prefix}row"),
                column = getIntOrNull("${prefix}location.column") ?: getInt("${prefix}column")
            )
        )
    }
}

/**
 * Add a page changer button placed directly from a config section.
 *
 * Reads the combined shape (`basePath.item` and the optional `basePath.item-inactive`)
 * or the split shape (`basePath.item.active` and `basePath.item.inactive`, with optional
 * `name` and `lore` per state), and `material` as a fallback for the active item.
 * The location is read with [getSlotLocations], so a `basePath.locations` list places
 * the button in every listed slot. If no active item is configured and no [defaultItem]
 * is given, the changer is not added, so a missing button stays hidden rather than
 * rendering a broken item.
 *
 * @param config The config holding the button section.
 * @param basePath The path of the button section.
 * @param direction The direction.
 * @param sound The page turn sound, or null for silent.
 * @param layer The layer, defaults to [MenuLayer.TOP].
 * @param defaultItem The item lookup string to use as the active item when none is configured,
 * or null to hide the button.
 * @param name The display name of both states, used instead of the config's when set.
 * @param defaultLocations The locations when the config sets none.
 * @param format Applied to the item lookup strings and to every name and lore line read from config.
 * @param hideInactive Whether to hide the button on the first or last page even when an inactive item is configured.
 * @return The builder.
 */
fun MenuBuilder.addPageChanger(
    config: Config,
    basePath: String,
    direction: PageChanger.Direction,
    sound: AbstractPlayableSound<*>?,
    layer: Int = MenuLayer.TOP,
    defaultItem: String? = null,
    name: String? = null,
    defaultLocations: List<SlotLocation>? = null,
    format: (String) -> String = { it },
    hideInactive: Boolean = false
): MenuBuilder {
    val section = config.getSubsection(basePath)

    if (defaultItem == null && !section.hasActiveItem()) {
        return this
    }

    val active = section.getConfigItem(defaultItem, name, state = "active", format = format)

    val inactive = when {
        hideInactive -> {
            null
        }
        section.has("item-inactive") -> {
            val item = Items.lookup(format(section.getString("item-inactive"))).item
            if (name == null) item else ItemStackBuilder(item).setDisplayName(name).build()
        }
        section.getPlainStringOrNull("item.inactive") != null -> {
            section.getConfigItem(name = name, state = "inactive", format = format)
        }
        else -> {
            null
        }
    }

    for ((row, column) in config.getSlotLocations(basePath, defaultLocations)) {
        addPageChanger(direction, active, inactive, sound, row, column, layer)
    }

    return this
}

/**
 * Add a page changer button placed directly from a config section.
 *
 * Kept so plugins compiled against 2026.42 and earlier still link.
 *
 * @param config The config holding the button section.
 * @param basePath The path of the button section.
 * @param direction The direction.
 * @param sound The page turn sound, or null for silent.
 * @param layer The layer, defaults to [MenuLayer.TOP].
 * @return The builder.
 */
@Deprecated("Use the overload with slot location and item options", level = DeprecationLevel.HIDDEN)
@ApiStatus.ScheduledForRemoval(inVersion = "2027.43")
fun MenuBuilder.addPageChanger(
    config: Config,
    basePath: String,
    direction: PageChanger.Direction,
    sound: AbstractPlayableSound<*>?,
    layer: Int = MenuLayer.TOP
): MenuBuilder = addPageChanger(config, basePath, direction, sound, layer, defaultItem = null)

private fun Config.hasActiveItem(): Boolean =
    getPlainStringOrNull("item.active") != null
        || getPlainStringOrNull("item") != null
        || getStringOrNull("material") != null

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
    sound: AbstractPlayableSound<*>?,
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

/** Kotlin builder for dispenser menus. */
fun dispenserMenu(
    init: MenuBuilder.() -> Unit
): Menu {
    val builder = Menu.builder(MenuType.DISPENSER)
    init(builder)
    return builder.build()
}
