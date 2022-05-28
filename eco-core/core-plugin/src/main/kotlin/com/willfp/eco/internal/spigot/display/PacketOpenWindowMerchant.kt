package com.willfp.eco.internal.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.proxy.VillagerTradeProxy
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

class PacketOpenWindowMerchant(plugin: EcoPlugin) :
    AbstractPacketAdapter(plugin,
        PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
        if (plugin.configYml.getBool("use-lower-protocollib-priority")) ListenerPriority.NORMAL else ListenerPriority.MONITOR,
        true) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        val recipes = mutableListOf<MerchantRecipe>()

        for (recipe in packet.merchantRecipeLists.read(0)) {
            val newRecipe = getPlugin().getProxy(VillagerTradeProxy::class.java).displayTrade(
                recipe!!, player
            )
            recipes.add(newRecipe)
        }
        packet.merchantRecipeLists.write(0, recipes)
    }
}