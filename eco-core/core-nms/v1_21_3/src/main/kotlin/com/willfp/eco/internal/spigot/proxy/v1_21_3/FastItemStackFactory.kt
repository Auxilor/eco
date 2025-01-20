package com.willfp.eco.internal.spigot.proxy.v1_21_3

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import com.willfp.eco.internal.spigot.proxy.common.modern.RegistryAccessor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return NewEcoFastItemStack(itemStack, ModernRegistryAccessor)
    }

    private object ModernRegistryAccessor : RegistryAccessor {
        override fun <T> getRegistry(key: ResourceKey<Registry<T>>): Registry<T> {
            val server = Bukkit.getServer() as CraftServer
            val access = server.server.registryAccess()
            return access.get(key).get().value()
        }
    }
}
