package com.willfp.eco.internal.spigot.proxy.v1_21

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.CustomModelData
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return LegacyNewEcoFastItemStack(itemStack)
    }

    private class LegacyNewEcoFastItemStack(itemStack: ItemStack) :
        NewEcoFastItemStack(itemStack) {

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
}
