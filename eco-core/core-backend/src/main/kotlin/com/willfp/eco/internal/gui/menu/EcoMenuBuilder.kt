package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.slot.EcoFillerSlot
import com.willfp.eco.util.ListUtils
import com.willfp.eco.util.StringUtils
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class EcoMenuBuilder(private val rows: Int) : MenuBuilder {
    private var title = "Menu"
    private var maskSlots: List<MutableList<Slot?>>
    private val slots: List<MutableList<Slot>> = ListUtils.create2DList(rows, 9)
    private var onClose = Consumer { _: InventoryCloseEvent -> }

    override fun setTitle(title: String): MenuBuilder {
        this.title = StringUtils.format(title)
        return this
    }

    override fun setSlot(
        row: Int,
        column: Int,
        slot: Slot
    ): MenuBuilder {
        require(!(row < 1 || row > rows)) { "Invalid row number!" }
        require(!(column < 1 || column > 9)) { "Invalid column number!" }
        slots[row - 1][column - 1] = slot
        return this
    }

    override fun setMask(mask: FillerMask): MenuBuilder {
        maskSlots = mask.mask
        return this
    }

    override fun onClose(action: Consumer<InventoryCloseEvent>): MenuBuilder {
        onClose = action
        return this
    }

    override fun build(): Menu {
        val finalSlots = maskSlots
        for (i in slots.indices) {
            for (j in slots[i].indices) {
                val slot = slots[i][j]
                finalSlots[i][j] = slot
            }
        }
        for (finalSlot in finalSlots) {
            for (j in finalSlot.indices) {
                if (finalSlot[j] == null) {
                    finalSlot[j] = EcoFillerSlot(ItemStack(Material.AIR))
                }
            }
        }
        return EcoMenu(rows, finalSlots, title, onClose)
    }

    init {
        maskSlots = ListUtils.create2DList(rows, 9)
    }
}