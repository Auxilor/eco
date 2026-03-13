package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.item.trading.MerchantOffers
import java.util.Optional

object PacketOpenWindowMerchant : PacketListener {
    private val field = ClientboundMerchantOffersPacket::class.java
        .declaredFields
        .first { it.type == MerchantOffers::class.java }
        .apply { isAccessible = true }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundMerchantOffersPacket ?: return

        val offers = MerchantOffers()

        for (offer in packet.offers) {
            val costA = Display.display(
                offer.costA.copy().asBukkitStack(),
                event.player
            )
            val costB = if (offer.costB.isPresent) Display.display(
                offer.costB.get().itemStack.copy().asBukkitStack(),
                event.player
            ) else null

            val result = Display.display(
                offer.result.copy().asBukkitStack(),
                event.player
            )

            val copy = MerchantOffer(
                ItemCost(costA.asNMSStack().item, offer.baseCostA.count),
                if (offer.costB.isPresent) Optional.of(
                    ItemCost(
                        costB!!.asNMSStack().item,
                        offer.costB.get().count
                    )
                ) else Optional.empty(),
                result.asNMSStack(),
                offer.uses,
                offer.maxUses,
                offer.xp,
                offer.priceMultiplier,
                offer.demand
            )

            offers += copy
        }

        field.set(packet, offers)
    }
}
