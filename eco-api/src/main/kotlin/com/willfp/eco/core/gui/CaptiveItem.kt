package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.properties.Delegates

/** Represents an item bound to a captive slot in a menu. */
class CaptiveItem {
    private var isBound = false

    private lateinit var menu: Menu
    private var row by Delegates.notNull<Int>()
    private var column by Delegates.notNull<Int>()

    /** Bind to a slot. */
    internal fun bind(menu: Menu, row: Int, column: Int) {
        require(!isBound) { "Already bound!" }

        this.menu = menu
        this.row = row
        this.column = column

        isBound = true
    }

    /** Get the item for a player. */
    operator fun get(player: Player): ItemStack? {
        if (!isBound) {
            return null
        }

        return menu.getCaptiveItem(player, row, column)
    }
}

/** Set a [slot] at a [row] and [column] and optionally bind it to an item. */
fun MenuBuilder.setSlot(
    row: Int,
    column: Int,
    slot: Slot,
    bindCaptive: CaptiveItem? = null
) = addComponent(row, column, slot, bindCaptive = bindCaptive)

/** @see addComponent */
fun MenuBuilder.addComponent(
    row: Int,
    column: Int,
    slot: Slot,
    bindCaptive: CaptiveItem? = null
) = addComponent(MenuLayer.MIDDLE, row, column, slot, bindCaptive = bindCaptive)

/**
 * Set a [component] at a [row] and [column] on a [layer] and optionally
 * bind it to an item.
 */
fun MenuBuilder.addComponent(
    layer: Int,
    row: Int,
    column: Int,
    component: GUIComponent,
    bindCaptive: CaptiveItem? = null
) {
    this.addComponent(
        layer,
        row,
        column,
        component
    )

    if (bindCaptive != null) {
        this.bind(
            row,
            column,
            bindCaptive
        )
    }
}

/** Bind a [captiveItem] to a [row] and [column]. */
fun MenuBuilder.bind(
    row: Int,
    column: Int,
    captiveItem: CaptiveItem
) {
    this.onBuild {
        captiveItem.bind(it, row, column)
    }
}
