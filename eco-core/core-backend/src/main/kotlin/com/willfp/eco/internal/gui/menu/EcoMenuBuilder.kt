package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuEventHandler
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.menu.OpenHandler
import com.willfp.eco.util.StringUtils
import org.bukkit.entity.Player
import java.util.function.BiConsumer
import java.util.function.Consumer

class EcoMenuBuilder(
    private val rows: Int,
    private val columns: Int
) : MenuBuilder {
    private var title = "Menu"
    private val components = mutableMapOf<MenuLayer, MutableMap<GUIPosition, MutableList<GUIComponent>>>()
    private val onClose = mutableListOf<CloseHandler>()
    private val onOpen = mutableListOf<OpenHandler>()
    private val onRender = mutableListOf<(Player, Menu) -> Unit>()
    private val menuEventHandlers = mutableListOf<MenuEventHandler<*>>()
    private var allowsChangingHeldItem = false

    override fun getRows() = rows
    override fun getColumns() = columns

    override fun setTitle(title: String): MenuBuilder {
        this.title = StringUtils.format(title)
        return this
    }

    override fun getTitle(): String {
        return title
    }

    override fun addComponent(layer: MenuLayer, row: Int, column: Int, component: GUIComponent): MenuBuilder {
        require(row in 1..rows) { "Invalid row number!" }
        require(column in 1..columns) { "Invalid column number!" }

        val maxRows = 1 + rows - row
        val maxColumns = 10 - column

        component.init(maxRows, maxColumns)

        require(column + component.columns - 1 <= columns) { "Component is too large to be placed here!" }
        require(row + component.rows - 1 <= rows) { "Component is too large to be placed here!" }

        val guiPosition = GUIPosition(row, column)
        components.computeIfAbsent(layer) { mutableMapOf() }
            .computeIfAbsent(guiPosition) { mutableListOf() } += component

        return this
    }

    override fun modfiy(modifier: Consumer<MenuBuilder>): MenuBuilder {
        modifier.accept(this)
        return this
    }

    override fun onClose(action: CloseHandler): MenuBuilder {
        onClose += action
        return this
    }

    override fun onOpen(action: OpenHandler): MenuBuilder {
        onOpen += action
        return this
    }

    override fun onRender(action: BiConsumer<Player, Menu>): MenuBuilder {
        onRender += { a, b -> action.accept(a, b) }
        return this
    }

    override fun onEvent(action: MenuEventHandler<*>): MenuBuilder {
        menuEventHandlers += action
        return this
    }

    override fun allowChangingHeldItem(): MenuBuilder {
        allowsChangingHeldItem = true
        return this
    }

    override fun build(): Menu {
        val layeredComponents = mutableMapOf<MenuLayer, MutableMap<GUIPosition, MutableList<OffsetComponent>>>()

        // 5 nested for loops? Shut up. Silence. Quiet.
        for (layer in MenuLayer.values()) {
            for (row in (1..rows)) {
                for (column in (1..columns)) {
                    for ((anchor, availableComponents) in components.computeIfAbsent(layer) { mutableMapOf() }) {
                        for (component in availableComponents) {
                            val rowOffset = 1 + row - anchor.row
                            val columnOffset = 1 + column - anchor.column

                            if (rowOffset <= 0 || columnOffset <= 0) {
                                continue
                            }

                            if (rowOffset > component.rows || columnOffset > component.columns) {
                                continue
                            }

                            val point = GUIPosition(row, column)

                            layeredComponents.computeIfAbsent(layer) { mutableMapOf() }
                                .computeIfAbsent(point) { mutableListOf() } += OffsetComponent(
                                component,
                                rowOffset,
                                columnOffset
                            )
                        }
                    }
                }
            }
        }

        val componentsAtPoints = mutableMapOf<GUIPosition, MutableList<OffsetComponent>>()

        for (menuLayer in MenuLayer.values()) {
            for ((anchor, offsetComponents) in layeredComponents[menuLayer] ?: emptyMap()) {
                componentsAtPoints[anchor] = offsetComponents
            }
        }

        return EcoMenu(
            rows,
            columns,
            componentsAtPoints,
            title,
            onClose,
            onRender,
            onOpen,
            menuEventHandlers,
            allowsChangingHeldItem
        )
    }
}
