package com.willfp.eco.proxy.v1_17_R1

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.proxy.v1_17_R1.fast.NMSFastItemStack
import org.bukkit.inventory.ItemStack
import com.willfp.eco.proxy.FastItemStackFactoryProxy

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return NMSFastItemStack(itemStack)
    }
}