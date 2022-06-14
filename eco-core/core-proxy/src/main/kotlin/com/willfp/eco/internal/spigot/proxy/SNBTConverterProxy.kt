package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.items.TestableItem
import org.bukkit.inventory.ItemStack

interface SNBTConverterProxy {
    fun toSNBT(itemStack: ItemStack): String
    fun fromSNBT(snbt: String): ItemStack?
    fun makeSNBTTestable(snbt: String): TestableItem
}