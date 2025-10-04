package com.willfp.eco.internal.spigot.proxy.v1_21_9

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxies.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.ModernEcoFastItemStack
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return ModernEcoFastItemStack(itemStack)
    }
}
