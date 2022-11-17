package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.util.toSingletonList
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class EcoCaptiveSlot(
    provider: SlotProvider,
    private val captiveFromEmpty: Boolean,
    private val notCaptiveFor: (Player, ItemStack?) -> Boolean
) : EcoSlot(
    provider,
    ClickType.values().associateWith {
        captiveWithTest(notCaptiveFor).toSingletonList()
    },
    { _, _, prev -> prev }
) {
    override fun isCaptive(player: Player, menu: Menu, itemStack: ItemStack?): Boolean {
        return !notCaptiveFor(player, itemStack)
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun captiveWithTest(itemTest: (Player, ItemStack?) -> Boolean): SlotHandler = SlotHandler { event, _, _ ->
    event.isCancelled = itemTest(event.whoClicked as Player, event.cursor)
}
