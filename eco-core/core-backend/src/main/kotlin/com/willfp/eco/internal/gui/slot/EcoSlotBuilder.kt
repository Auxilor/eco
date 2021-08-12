package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotModifier
import com.willfp.eco.core.gui.slot.functional.SlotProvider

class EcoSlotBuilder(private val provider: SlotProvider) : SlotBuilder {
    private var captive = false
    var modifier: SlotModifier = SlotModifier{ player, menu, _ -> provider.provide(player, menu)}

    private var onLeftClick =
        SlotHandler { _, _, _ -> run { } }
    private var onRightClick =
        SlotHandler { _, _, _ -> run { } }
    private var onShiftLeftClick =
        SlotHandler { _, _, _ -> run { } }
    private var onShiftRightClick =
        SlotHandler { _, _, _ -> run { } }
    private var onMiddleClick =
        SlotHandler { _, _, _ -> run { } }

    override fun onLeftClick(action: SlotHandler): SlotBuilder {
        onLeftClick = action
        return this
    }

    override fun onRightClick(action: SlotHandler): SlotBuilder {
        onRightClick = action
        return this
    }

    override fun onShiftLeftClick(action: SlotHandler): SlotBuilder {
        onShiftLeftClick = action
        return this
    }

    override fun onShiftRightClick(action: SlotHandler): SlotBuilder {
        onShiftRightClick = action
        return this
    }

    override fun onMiddleClick(action: SlotHandler): SlotBuilder {
        onMiddleClick = action
        return this
    }

    override fun setCaptive(): SlotBuilder {
        captive = true
        return this
    }

    override fun setModifier(modifier: SlotModifier): SlotBuilder {
        this.modifier = modifier
        return this
    }

    override fun build(): Slot {
        return if (captive) {
            EcoCaptivatorSlot()
        } else {
            EcoSlot(provider, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick, modifier)
        }
    }
}