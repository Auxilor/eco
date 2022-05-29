package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider

class EcoCaptiveSlot(
    provider: SlotProvider,
    private val captiveFromEmpty: Boolean,
    onLeftClick: SlotHandler,
    onRightClick: SlotHandler,
    onShiftLeftClick: SlotHandler,
    onShiftRightClick: SlotHandler,
    onMiddleClick: SlotHandler,
) : EcoSlot(
    provider,
    onLeftClick.captiveIfNoop(),
    onRightClick.captiveIfNoop(),
    onShiftLeftClick.captiveIfNoop(),
    onShiftRightClick.captiveIfNoop(),
    onMiddleClick.captiveIfNoop(),
    { _, _, prev -> prev }
) {
    override fun isCaptive(): Boolean {
        return true
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun SlotHandler.captiveIfNoop(): SlotHandler {
    return if (this == NoOpSlot) {
        allowMovingItem
    } else {
        this
    }
}

private val allowMovingItem = SlotHandler { event, _, _ ->
    event.isCancelled = false
}
