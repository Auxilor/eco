package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider

class EcoCaptiveSlot(
    provider: SlotProvider
) : EcoSlot(
    provider,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    { _, _, prev -> prev }
) {
    override fun isCaptive(): Boolean {
        return true
    }
}

private val allowMovingItem = SlotHandler { event, _, _ ->
    event.isCancelled = false
}