package com.willfp.eco.internal.spigot.proxy.v26_2.packet

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
import net.minecraft.world.item.trading.MerchantOffers
import org.bukkit.craftbukkit.inventory.CraftItemStack

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

            Display.display(CraftItemStack.asCraftMirror(new.baseCostA.itemStack), event.player)
            if (new.costB.isPresent) {
                Display.display(CraftItemStack.asCraftMirror(new.costB.get().itemStack), event.player)
            }
            Display.display(CraftItemStack.asCraftMirror(new.getResult()), event.player)

            offers += new
        }

        field.set(packet, offers)
    }
}
