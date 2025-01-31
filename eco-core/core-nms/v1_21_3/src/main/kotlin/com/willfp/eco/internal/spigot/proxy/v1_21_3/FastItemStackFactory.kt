package com.willfp.eco.internal.spigot.proxy.v1_21_3

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return NewEcoFastItemStack(itemStack)
    }
}
