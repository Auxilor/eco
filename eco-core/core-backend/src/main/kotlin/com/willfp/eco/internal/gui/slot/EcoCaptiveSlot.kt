package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.util.toSingletonList
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EcoCaptiveSlot(
    provider: SlotProvider,
    private val captiveFromEmpty: Boolean,
    private val notCaptiveFor: (Player) -> Boolean
) : EcoSlot(
    provider,
    ClickType.values().associateWith {
        captiveWithTest(notCaptiveFor).toSingletonList()
    },
    { _, _, prev -> prev }
) {
    override fun isCaptive(player: Player, menu: Menu): Boolean {
        return !notCaptiveFor(player)
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun captiveWithTest(test: (Player) -> Boolean): SlotHandler {
    return SlotHandler { event, _, _ ->
        event.isCancelled = test(event.whoClicked as Player)
    }
}
