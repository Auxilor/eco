package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.gui.slot.functional.SlotUpdater
import com.willfp.eco.util.openMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

open class EcoSlot(
    private val provider: SlotProvider,
    private val handlers: Map<ClickType, List<SlotHandler>>,
    private val updater: SlotUpdater
) : Slot {
    private fun List<SlotHandler>.handle(event: InventoryClickEvent, slot: Slot, menu: Menu) =
        this.forEach { it.handle(event, slot, menu) }

    fun handleInventoryClick(
        event: InventoryClickEvent,
        menu: Menu
    ) {
        handlers[event.click]?.handle(event, this, menu)
    }

    override fun getItemStack(player: Player): ItemStack {
        val menu = player.openMenu ?: return ItemStack(Material.AIR)
        val base = provider.provide(player, menu) ?: return ItemStack(Material.AIR)
        return updater.update(player, menu, base) ?: return ItemStack(Material.AIR)
    }

    override fun isCaptive(player: Player, menu: Menu): Boolean {
        return false
    }

    override fun getActionableSlot(player: Player, menu: Menu): EcoSlot = this
}
