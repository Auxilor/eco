package com.willfp.eco.internal.spigot.proxy.v1_21_7.packet

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
import net.minecraft.world.item.trading.MerchantOffers

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

            Display.display(new.baseCostA.itemStack.asBukkitStack(), event.player)
            if (new.costB.isPresent) {
                Display.display(new.costB.get().itemStack.asBukkitStack(), event.player)
            }
            Display.display(new.result.asBukkitStack(), event.player)

            offers += new
        }

        field.set(packet, offers)
    }
}
