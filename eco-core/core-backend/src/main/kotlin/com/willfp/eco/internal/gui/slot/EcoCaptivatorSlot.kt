package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Function

class EcoCaptivatorSlot(
    provider: Function<Player, ItemStack>
) : EcoSlot(
    provider,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem
) {
    var captive: ItemStack? = null

    override fun getItemStack(player: Player): ItemStack {
        return captive ?: provider.apply(player);
    }

    companion object {
        val plugin = Eco.getHandler().ecoPlugin!!

        val allowMovingItem: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { event, _ ->
            event.isCancelled = false
        }
    }
}