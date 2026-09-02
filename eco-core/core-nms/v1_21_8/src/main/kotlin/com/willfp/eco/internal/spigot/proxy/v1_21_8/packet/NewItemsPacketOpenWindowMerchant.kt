package com.willfp.eco.internal.spigot.proxy.v1_21_8.packet

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import net.minecraft.core.component.DataComponentExactPredicate
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffers
import org.bukkit.entity.Player

object NewItemsPacketOpenWindowMerchant : PacketListener {
    private val field = ClientboundMerchantOffersPacket::class.java
        .declaredFields
        .first { it.type == MerchantOffers::class.java }
        .apply { isAccessible = true }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundMerchantOffersPacket ?: return

        val offers = MerchantOffers()

        for (offer in packet.offers) {
            val new = offer.copy()

            // MerchantOffer#copy only copies the result stack - the costs are still the live
            // offer's ItemCost objects, so they're replaced here rather than displayed in place.
            // Displaying them in place would write display lore into the trade the server
            // matches against, on every open.
            new.baseCostA = new.baseCostA.displayed(event.player)
            new.costB = new.costB.map { it.displayed(event.player) }

            Display.display(new.result.asBukkitStack(), event.player)

            offers += new
        }

        field.set(packet, offers)
    }

    /**
     * The same cost, with its component predicate rebuilt from the displayed item.
     *
     * The client runs MerchantContainer#updateSellItem itself and fills its own result slot,
     * testing the items it holds - which eco has displayed - against this predicate. Only the
     * predicate crosses the wire (ItemCost's stream codec is item, count, predicate; its
     * ItemStack is rebuilt client-side), so a predicate taken from the undisplayed item never
     * matches a displayed one: the client shows an empty result slot for a trade the server is
     * happy to complete, for every item whose display touches a component. Sending the displayed
     * components has both sides testing the same thing.
     */
    private fun ItemCost.displayed(player: Player): ItemCost {
        val displayed = this.itemStack.copy()
        Display.display(displayed.asBukkitStack(), player)

        val components = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, displayed.componentsPatch)

        return ItemCost(displayed.itemHolder, this.count, DataComponentExactPredicate.allOf(components), displayed)
    }
}
