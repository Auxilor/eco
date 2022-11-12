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
    private val notCaptiveFor: (Player) -> Boolean,
    private val notCaptiveForItem: (Player, ItemStack?) -> Boolean
) : EcoSlot(
    provider,
    ClickType.values().associateWith {
        captiveWithTest(notCaptiveFor, notCaptiveForItem).toSingletonList()
    },
    { _, _, prev -> prev }
) {
    override fun isCaptive(player: Player, menu: Menu): Boolean {
        return !notCaptiveFor(player)
    }

    override fun isCaptiveForItem(player: Player, menu: Menu, itemStack: ItemStack?): Boolean {
        return !notCaptiveForItem(player, itemStack)
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun captiveWithTest(test: (Player) -> Boolean,
                            itemTest: (Player, ItemStack?) -> Boolean): SlotHandler {
    return SlotHandler { event, _, _ ->
        event.isCancelled = itemTest(event.whoClicked as Player, event.cursor) || test(event.whoClicked as Player)
    }
}
