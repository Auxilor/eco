package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Function

open class EcoSlot(
    val provider: Function<Player, ItemStack>,
    private val onLeftClick: BiConsumer<InventoryClickEvent, Slot>,
    private val onRightClick: BiConsumer<InventoryClickEvent, Slot>,
    private val onShiftLeftClick: BiConsumer<InventoryClickEvent, Slot>,
    private val onShiftRightClick: BiConsumer<InventoryClickEvent, Slot>,
    private val onMiddleClick: BiConsumer<InventoryClickEvent, Slot>
) : Slot {

    fun handleInventoryClick(event: InventoryClickEvent) {
        when (event.click) {
            ClickType.LEFT -> this.onLeftClick.accept(event, this)
            ClickType.RIGHT -> this.onRightClick.accept(event, this)
            ClickType.SHIFT_LEFT -> this.onShiftLeftClick.accept(event, this)
            ClickType.SHIFT_RIGHT -> this.onShiftRightClick.accept(event, this)
            ClickType.MIDDLE -> this.onMiddleClick.accept(event, this)
            else -> { }
        }
    }

    override fun getItemStack(player: Player): ItemStack {
        return provider.apply(player)
    }
}