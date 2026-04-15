package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player

class LayeredComponents {
    // Max 6 rows * 9 columns = 54 slots
    private val layers = mutableMapOf<MenuLayer, Array<MutableList<OffsetComponent>?>>()

    fun getSlotAt(row: Int, column: Int, player: Player?, menu: Menu): Slot {
        val index = (row - 1) * 9 + (column - 1)

        for (layer in MenuLayer.entries.reversed()) {
            val slotArray = layers[layer] ?: continue

            if (index < 0 || index >= slotArray.size) continue

            val components = slotArray[index] ?: continue

            for (component in components) {
                val found = if (player != null) component.component.getSlotAt(
                    component.rowOffset,
                    component.columnOffset,
                    player,
                    menu
                ) else component.component.getSlotAt(
                    component.rowOffset,
                    component.columnOffset
                )

                if (found != null) {
                    return found
                }
            }
        }

        return emptyFillerSlot
    }

    fun getComponent(row: Int, column: Int, layer: MenuLayer): List<OffsetComponent>? {
        val index = (row - 1) * 9 + (column - 1)
        val slotArray = layers[layer] ?: return null
        if (index < 0 || index >= slotArray.size) return null
        return slotArray[index]
    }

    fun setComponent(row: Int, column: Int, layer: MenuLayer, component: OffsetComponent) {
        addOffsetComponent(layer, GUIPosition(row, column), component)
    }

    fun addOffsetComponent(layer: MenuLayer, position: GUIPosition, component: OffsetComponent) {
        val index = (position.row - 1) * 9 + (position.column - 1)
        val slotArray = layers.getOrPut(layer) { arrayOfNulls(54) }
        val list = slotArray[index]
        if (list != null) {
            list.add(component)
        } else {
            slotArray[index] = mutableListOf(component)
        }
    }
}
