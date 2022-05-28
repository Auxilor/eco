package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.items.TestableItem
import org.bukkit.entity.Player

class EcoCaptiveSlot(
    provider: SlotProvider,
    private val captiveDefault: ((Player) -> TestableItem?)?
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

    override fun getCaptiveDefault(player: Player): TestableItem? {
        return captiveDefault?.invoke(player)
    }
}

private val allowMovingItem = SlotHandler { event, _, _ ->
    event.isCancelled = false
}
