package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.CloseHandler
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.OpenHandler
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.FillerSlot
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.internal.gui.slot.EcoFillerSlot
import com.willfp.eco.internal.gui.slot.EcoSlot
import com.willfp.eco.util.ListUtils
import com.willfp.eco.util.StringUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Consumer

class EcoMenuBuilder(private val rows: Int) : MenuBuilder {
    private var title = "Menu"
    private var maskSlots: List<MutableList<Slot?>>
    private val slots: List<MutableList<Slot?>> = ListUtils.create2DList(rows, 9)
    private var onClose = CloseHandler { _, _ -> }
    private var onOpen = OpenHandler { _, _ -> }
    private var onRender: (Player, Menu) -> Unit = { _, _ -> }

    override fun getRows() = rows

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
        val tempSlots: MutableList<MutableList<Slot?>> = ArrayList(maskSlots)

        for (i in slots.indices) {
            for (j in slots[i].indices) {
                val slot = slots[i][j] ?: continue
                tempSlots[i][j] = slot
            }
        }

        val finalSlots = mutableListOf<MutableList<EcoSlot>>()

        for (row in tempSlots) {
            val tempRow = mutableListOf<EcoSlot>()
            for (slot in row) {
                var tempSlot = slot
                if (tempSlot is FillerSlot) {
                    tempSlot = EcoFillerSlot(tempSlot.itemStack)
                }
                tempRow.add((tempSlot ?: EcoFillerSlot(ItemStack(Material.AIR))) as EcoSlot)
            }
            finalSlots.add(tempRow)
        }

        return EcoMenu(rows, finalSlots, title, onClose, onRender, onOpen)
    }

    init {
        maskSlots = ListUtils.create2DList(rows, 9)
    }
}