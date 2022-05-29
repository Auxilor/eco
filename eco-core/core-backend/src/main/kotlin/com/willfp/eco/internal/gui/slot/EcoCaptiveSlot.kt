package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import org.bukkit.entity.Player

class EcoCaptiveSlot(
    provider: SlotProvider,
    private val captiveFromEmpty: Boolean,
    private val notCaptiveFor: (Player) -> Boolean
) : EcoSlot(
    provider,
    captiveWithTest(notCaptiveFor),
    captiveWithTest(notCaptiveFor),
    captiveWithTest(notCaptiveFor),
    captiveWithTest(notCaptiveFor),
    captiveWithTest(notCaptiveFor),
    { _, _, prev -> prev }
) {
    override fun isCaptive(): Boolean {
        return true
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }

    override fun isNotCaptiveFor(player: Player): Boolean {
        return notCaptiveFor(player)
    }
}

private fun captiveWithTest(test: (Player) -> Boolean): SlotHandler {
    return SlotHandler { event, _, _ ->
        event.isCancelled = test(event.whoClicked as Player)
    }
}
