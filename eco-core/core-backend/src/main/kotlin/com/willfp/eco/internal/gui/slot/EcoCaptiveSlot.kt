package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.CaptiveFilter
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.util.toSingletonList
import org.bukkit.Material
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
    ClickType.entries.associateWith {
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
    notCaptiveFor: (Player) -> Boolean,
    filter: CaptiveFilter
): SlotHandler = SlotHandler { event, _, menu ->
    val player = event.whoClicked as Player

    val item = event.currentItem.nullIfAir() ?: event.cursor.nullIfAir()

    event.isCancelled = !filter.isAllowed(player, menu, item) || notCaptiveFor(player)
}

private fun ItemStack?.nullIfAir(): ItemStack? =
    if (this?.type == Material.AIR) null else this
