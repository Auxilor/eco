package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.CaptiveCondition
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
    private val condition: CaptiveCondition
) : EcoSlot(
    provider,
    ClickType.values().associateWith {
        captiveWithTest(notCaptiveFor, condition).toSingletonList()
    },
    { _, _, prev -> prev }
) {
    override fun isCaptive(player: Player, menu: Menu): Boolean {
        return !notCaptiveFor(player)
    }

    override fun canCaptivateItem(player: Player, menu: Menu, itemStack: ItemStack?): Boolean {
        return condition.isCaptive(player, menu, itemStack)
    }

    override fun isCaptiveFromEmpty(): Boolean {
        return captiveFromEmpty
    }
}

private fun captiveWithTest(
    playerTest: (Player) -> Boolean,
    condition: CaptiveCondition
): SlotHandler = SlotHandler { event, _, menu ->
    val player = event.whoClicked as Player

    val allowedForPlayer = !playerTest(player)
    val allowedForCondition = condition.isCaptive(player, menu, event.currentItem)

    event.isCancelled = !(allowedForCondition && allowedForPlayer)
}
