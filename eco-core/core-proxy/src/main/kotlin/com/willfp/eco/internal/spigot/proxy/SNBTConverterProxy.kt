package com.willfp.eco.internal.spigot.proxy

import org.bukkit.inventory.ItemStack

interface SNBTConverterProxy {
    fun toSNBT(itemStack: ItemStack): String
    fun fromSNBT(snbt: String): ItemStack?
}