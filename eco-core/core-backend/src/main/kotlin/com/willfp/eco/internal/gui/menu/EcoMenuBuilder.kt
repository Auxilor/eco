package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.component.GUIComponent
import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.OpenHandler
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.util.ListUtils
import com.willfp.eco.util.StringUtils
import org.bukkit.entity.Player
import java.util.function.BiConsumer
import java.util.function.Consumer

class EcoMenuBuilder(private val rows: Int) : MenuBuilder {
    private var title = "Menu"
    private var maskSlots: List<MutableList<Slot?>>
    private val components = mutableMapOf<Anchor, GUIComponent>()
    private var onClose = CloseHandler { _, _ -> }
    private var onOpen = OpenHandler { _, _ -> }
    private var onRender: (Player, Menu) -> Unit = { _, _ -> }

    override fun getRows() = rows

    override fun setTitle(title: String): MenuBuilder {
        this.title = StringUtils.format(title)
        return this
    }

    override fun addComponent(row: Int, column: Int, component: GUIComponent): MenuBuilder {
        require(!(row < 1 || row > rows)) { "Invalid row number!" }
        require(!(column < 1 || column > 9)) { "Invalid column number!" }
        require(column + component.columns - 1 <= 9) { "Component is too large to be placed here!" }
        require(row + component.rows - 1 <= getRows()) { "Component is too large to be placed here!" }

        components[Anchor(row, column)] = component
        return this
    }

    override fun modfiy(modifier: Consumer<MenuBuilder>): MenuBuilder {
        modifier.accept(this)
        return this
    }

    override fun setMask(mask: FillerMask): MenuBuilder {
        maskSlots = mask.mask
        return this
    }

    override fun onClose(action: CloseHandler): MenuBuilder {
        onClose = action
        return this
    }

    override fun onOpen(action: OpenHandler): MenuBuilder {
        onOpen = action
        return this
    }

    override fun onRender(action: BiConsumer<Player, Menu>): MenuBuilder {
        onRender = { a, b -> action.accept(a, b) }
        return this
    }

    override fun build(): Menu {
        val slots = mutableMapOf<Anchor, Slot>()

        for (row in (1..rows)) {
            for (column in (1..9)) {
                for ((anchor, component) in components) {
                    // Too far to the top / left to be in bounds
                    if (anchor.row > row || anchor.column > column) {
                        continue
                    }

                    // Too far to the bottom / left to be in bounds
                    if (row > anchor.row + component.rows - 1 || column > anchor.column + component.columns - 1) {
                        continue
                    }

                    val rowOffset = anchor.row - row
                    val columnOffset = anchor.column - column

                    val slot = component.getSlotAt(rowOffset, columnOffset)
                    if (slot != null) {
                        slots[Anchor(row, column)] = slot
                    }
                }
            }
        }

        return EcoMenu(rows, slots, title, onClose, onRender, onOpen)
    }

    init {
        maskSlots = ListUtils.create2DList(rows, 9)
    }
}