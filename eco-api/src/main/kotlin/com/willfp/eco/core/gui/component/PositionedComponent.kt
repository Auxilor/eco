package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.page.PageBuilder

/** A component that knows where it goes, so a menu can place it with [addComponent]. */
interface PositionedComponent : GUIComponent {
    /** Every slot the component is placed at. */
    val locations: List<SlotLocation>

    /** If false, [addComponent] adds nothing. */
    val isEnabled: Boolean
        get() = true

    /** The layer to place the component on, see [MenuLayer]. */
    val layer: Int
        get() = MenuLayer.MIDDLE

    /** The height of the component. */
    val rowSize: Int
        get() = 1

    /** The width of the component. */
    val columnSize: Int
        get() = 1

    override fun getRows() = rowSize

    override fun getColumns() = columnSize
}

/** Add [component] at every one of its locations, or nothing if it is not enabled. */
fun MenuBuilder.addComponent(component: PositionedComponent): MenuBuilder =
    addComponent(component.layer, component)

/** Add [component] on [layer] at every one of its locations, or nothing if it is not enabled. */
fun MenuBuilder.addComponent(layer: Int, component: PositionedComponent): MenuBuilder {
    if (component.isEnabled) {
        for ((row, column) in component.locations) {
            addComponent(layer, row, column, component)
        }
    }

    return this
}

/** Add [component] at every one of its locations, or nothing if it is not enabled. */
fun PageBuilder.addComponent(component: PositionedComponent): PageBuilder =
    addComponent(component.layer, component)

/** Add [component] on [layer] at every one of its locations, or nothing if it is not enabled. */
fun PageBuilder.addComponent(layer: Int, component: PositionedComponent): PageBuilder {
    if (component.isEnabled) {
        for ((row, column) in component.locations) {
            addComponent(layer, row, column, component)
        }
    }

    return this
}
