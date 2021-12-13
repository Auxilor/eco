package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.inventory.ItemStack

interface FastItemStackFactoryProxy {
    fun create(itemStack: ItemStack): FastItemStack
}