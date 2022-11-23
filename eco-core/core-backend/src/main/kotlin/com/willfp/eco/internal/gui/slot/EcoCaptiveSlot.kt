package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.CaptiveFilter
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
    private val filter: CaptiveFilter
) : EcoSlot(
    provider,
    ClickType.values().associateWith {
        captiveWithTest(notCaptiveFor, filter).toSingletonList()
    },
    { _, _, prev -> prev }
) {
    override fun isCaptive(player: Player, menu: Menu): Boolean {
        return !notCaptiveFor(player)
    }

    override fun isAllowedCaptive(player: Player, menu: Menu, itemStack: ItemStack?): Boolean {
        return filter.isAllowed(player, menu, itemStack)
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun captiveWithTest(
    playerTest: (Player) -> Boolean,
    filter: CaptiveFilter
): SlotHandler = SlotHandler { event, _, menu ->
    val player = event.whoClicked as Player

    val allowedForPlayer = !playerTest(player)
    val allowedForCondition = filter.isAllowed(player, menu, event.currentItem)

    event.isCancelled = !(allowedForCondition && allowedForPlayer)
}
