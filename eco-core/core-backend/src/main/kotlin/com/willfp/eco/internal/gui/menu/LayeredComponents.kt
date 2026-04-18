package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player

class LayeredComponents {
    private val layers = mutableMapOf<MenuLayer, Map<GUIPosition, List<OffsetComponent>>>()

    fun getSlotAt(row: Int, column: Int, player: Player?, menu: Menu): Slot {
        val guiPosition = GUIPosition(row, column)

        for (layer in MenuLayer.entries.reversed()) {
            val componentsAtPoints = layers[layer] ?: continue

            val components = componentsAtPoints[guiPosition] ?: continue

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

    fun addOffsetComponent(layer: MenuLayer, position: GUIPosition, component: OffsetComponent) {
        val inLayer = layers[layer]?.toMutableMap() ?: mutableMapOf()
        val atPosition = inLayer[position]?.toMutableList() ?: mutableListOf()
        atPosition.add(component)
        inLayer[position] = atPosition
        layers[layer] = inLayer
    }
}
