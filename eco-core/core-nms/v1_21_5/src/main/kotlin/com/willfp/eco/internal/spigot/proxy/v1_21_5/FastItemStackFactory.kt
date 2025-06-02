package com.willfp.eco.internal.spigot.proxy.v1_21_5

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import com.willfp.eco.internal.spigot.proxy.v1_21_5.item.NewerEcoFastItemStack
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return NewerEcoFastItemStack(itemStack)
    }
}
