package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotHandler
import com.willfp.eco.core.gui.slot.SlotProvider
import com.willfp.eco.internal.gui.menu.MenuHandler
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

open class EcoSlot(
    private val provider: SlotProvider,
    private val onLeftClick: SlotHandler,
    private val onRightClick: SlotHandler,
    private val onShiftLeftClick: SlotHandler,
    private val onShiftRightClick: SlotHandler,
    private val onMiddleClick: SlotHandler
) : Slot {

    fun handleInventoryClick(
        event: InventoryClickEvent,
        menu: Menu
    ) {
        when (event.click) {
            ClickType.LEFT -> this.onLeftClick.handle(event, this, menu)
            ClickType.RIGHT -> this.onRightClick.handle(event, this, menu)
            ClickType.SHIFT_LEFT -> this.onShiftLeftClick.handle(event, this, menu)
            ClickType.SHIFT_RIGHT -> this.onShiftRightClick.handle(event, this, menu)
            ClickType.MIDDLE -> this.onMiddleClick.handle(event, this, menu)
            else -> {
            }
        }
    }

    override fun getItemStack(player: Player): ItemStack {
        return provider.provide(player, MenuHandler.getMenu(player.openInventory.topInventory)!!)
    }

    fun getItemStack(
        player: Player,
        menu: Menu
    ): ItemStack {
        return provider.provide(player, menu)
    }

    override fun isCaptive(): Boolean {
        return false
    }
}