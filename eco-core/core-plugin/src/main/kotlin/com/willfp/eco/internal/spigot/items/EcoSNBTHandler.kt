package com.willfp.eco.internal.spigot.items

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.SNBTHandler
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import org.bukkit.inventory.ItemStack

class EcoSNBTHandler(
    private val plugin: EcoPlugin
) : SNBTHandler {
    override fun fromSNBT(snbt: String): ItemStack? =
        plugin.getProxy(SNBTConverterProxy::class.java).fromSNBT(snbt)

    override fun toSNBT(itemStack: ItemStack): String =
        plugin.getProxy(SNBTConverterProxy::class.java).toSNBT(itemStack)
}