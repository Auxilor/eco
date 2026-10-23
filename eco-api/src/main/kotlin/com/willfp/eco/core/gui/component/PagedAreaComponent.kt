package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.SlotArea
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.onRender
import com.willfp.eco.core.gui.page.Page
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player

/**
 * A grid of entries shown a page at a time.
 *
 * Fill order is row by row. A position with no entry shows [getEmptySlot].
 *
 * @param T The entry type.
 * @property area The area the grid covers.
 */
abstract class PagedAreaComponent<T>(
    val area: SlotArea
) : GUIComponent {
    /** Get every entry, across all pages. */
    abstract fun getEntries(player: Player, menu: Menu): List<T>

    /** Get the slot to show for [entry]. */
    abstract fun getEntrySlot(player: Player, menu: Menu, entry: T): Slot?

    /** Get the slot to show where there is no entry. */
    open fun getEmptySlot(player: Player, menu: Menu): Slot? = null

    /** Get the entry at a 1-indexed position of the area on the player's current page. */
    fun getEntryAt(player: Player, menu: Menu, row: Int, column: Int): T? =
        getEntries(player, menu).getOrNull(
            (row - 1) * area.columns + (column - 1) + area.size * (menu.getPage(player) - 1)
        )

    /** Get the number of pages, 0 when there are no entries or the area is empty. */
    fun getPages(player: Player, menu: Menu): Int {
        if (area.size == 0) {
            return 0
        }

        return (getEntries(player, menu).size + area.size - 1) / area.size
    }

    override fun getRows() = area.rows

    override fun getColumns() = area.columns

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? {
        val entry = getEntryAt(player, menu, row, column) ?: return getEmptySlot(player, menu)

        return getEntrySlot(player, menu, entry)
    }
}

/**
 * Add a [PagedAreaComponent] at its area, keeping the menu's max page in step with it.
 *
 * On each render the max page is set to the component's page count and the
 * current page is reset to 1 when it is above that.
 *
 * @param component The component.
 * @param layer The layer, defaults to [MenuLayer.MIDDLE].
 * @return The builder.
 */
fun MenuBuilder.addPagedArea(
    component: PagedAreaComponent<*>,
    layer: Int = MenuLayer.MIDDLE
): MenuBuilder {
    addComponent(layer, component.area.row, component.area.column, component)

    return onRender { player, menu ->
        val pages = component.getPages(player, menu)
        menu.setState(player, Page.MAX_PAGE_KEY, pages)

        if (menu.getPage(player) > pages) {
            menu.setState(player, Page.PAGE_KEY, 1)
        }
    }
}
