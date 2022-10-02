package com.willfp.eco.internal.spigot.items

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import org.bukkit.inventory.ItemStack

class EcoSNBTHandler(
    private val plugin: EcoPlugin
) {
    fun fromSNBT(snbt: String): ItemStack? =
        plugin.getProxy(SNBTConverterProxy::class.java).fromSNBT(snbt)

    fun toSNBT(itemStack: ItemStack): String =
        plugin.getProxy(SNBTConverterProxy::class.java).toSNBT(itemStack)

    fun createTestable(snbt: String): TestableItem =
        plugin.getProxy(SNBTConverterProxy::class.java).makeSNBTTestable(snbt)
}