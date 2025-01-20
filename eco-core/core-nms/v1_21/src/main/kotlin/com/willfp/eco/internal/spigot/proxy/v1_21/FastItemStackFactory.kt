package com.willfp.eco.internal.spigot.proxy.v1_21

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import com.willfp.eco.internal.spigot.proxy.common.modern.RegistryAccessor
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.component.CustomModelData
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return LegacyNewEcoFastItemStack(itemStack)
    }

    private class LegacyNewEcoFastItemStack(itemStack: ItemStack) :
        NewEcoFastItemStack(itemStack, LegacyRegistryAccessor) {

        override fun getCustomModelData(): Int? =
            handle.get(DataComponents.CUSTOM_MODEL_DATA)?.value

        override fun setCustomModelData(data: Int?) {
            if (data == null) {
                handle.remove(DataComponents.CUSTOM_MODEL_DATA)
            } else {
                handle.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(data))
            }

            apply()
        }
    }

    private object LegacyRegistryAccessor : RegistryAccessor {
        override fun <T> getRegistry(key: ResourceKey<Registry<T>>): Registry<T> {
            val server = Bukkit.getServer() as CraftServer
            val access = server.server.registryAccess()
            return access.registryOrThrow(key)
        }
    }
}
