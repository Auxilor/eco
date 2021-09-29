package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.proxy.VillagerTradeProxy
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
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


        /*
        This awful, awful bit of code exists to fix a bug that existed in EcoEnchants
        for too many versions.
         */
        if (getPlugin().configYml.getBool("villager-display-fix")) {
            for (recipe in packet.merchantRecipeLists.read(0)) {
                val result = recipe.result
                val meta = result.itemMeta
                if (meta != null) {
                    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                    meta.persistentDataContainer.remove(NamespacedKeyUtils.create("ecoenchants", "ecoenchantlore-skip"))
                    result.itemMeta = meta
                }
            }
        }
        for (recipe in packet.merchantRecipeLists.read(0)) {
            val newRecipe = getPlugin().getProxy(VillagerTradeProxy::class.java).displayTrade(
                recipe!!, player
            )
            recipes.add(newRecipe)
        }
        packet.merchantRecipeLists.write(0, recipes)
    }
}