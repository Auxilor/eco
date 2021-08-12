package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Function

class EcoSlotBuilder(private val provider: Function<Player, ItemStack>) : SlotBuilder {
    private var captive = false;

    private var onLeftClick: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { _, _ -> run { } }
    private var onRightClick: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { _, _ -> run { } }
    private var onShiftLeftClick: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { _, _ -> run { } }
    private var onShiftRightClick: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { _, _ -> run { } }
    private var onMiddleClick: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { _, _ -> run { } }

    override fun onLeftClick(action: BiConsumer<InventoryClickEvent, Slot>): SlotBuilder {
        onLeftClick = action
        return this
    }

    override fun onRightClick(action: BiConsumer<InventoryClickEvent, Slot>): SlotBuilder {
        onRightClick = action
        return this
    }

    override fun onShiftLeftClick(action: BiConsumer<InventoryClickEvent, Slot>): SlotBuilder {
        onShiftLeftClick = action
        return this
    }

    override fun onShiftRightClick(action: BiConsumer<InventoryClickEvent, Slot>): SlotBuilder {
        onShiftRightClick = action
        return this
    }

    override fun onMiddleClick(action: BiConsumer<InventoryClickEvent, Slot>): SlotBuilder {
        onMiddleClick = action
        return this
    }

    override fun setCaptive(): SlotBuilder {
        captive = true
        return this
    }

    override fun build(): Slot {
        return if (captive) {
            EcoCaptivatorSlot()
        } else {
            EcoSlot(provider, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick)
        }
    }
}