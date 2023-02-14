package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.trading.MerchantOffer

object PacketOpenWindowMerchant : PacketListener {
    private val field = ClientboundMerchantOffersPacket::class.java.getDeclaredField("b").apply { isAccessible = true }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundMerchantOffersPacket ?: return

        val offers = mutableListOf<MerchantOffer>()

        for (offer in packet.offers) {
            val nbt = offer.createTag()
            for (tag in arrayOf("buy", "buyB", "sell")) {
                val nms = ItemStack.of(nbt.getCompound(tag))
                val displayed = Display.display(nms.asBukkitStack(), event.player)
                val itemNBT = displayed.asNMSStack().save(CompoundTag())
                nbt.put(tag, itemNBT)
            }

            offers += MerchantOffer(nbt)
        }

        field.set(packet, offers)
    }
}
