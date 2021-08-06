package com.willfp.eco.proxy.v1_17_R1.fast

import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

class FastItemStackUtils {
    companion object {
        private var field: Field

        init {
            lateinit var temp: Field
            try {
                val handleField = CraftItemStack::class.java.getDeclaredField("handle")
                handleField.isAccessible = true
                temp = handleField
            } catch (e: ReflectiveOperationException) {
                e.printStackTrace()
            }
            field = temp
        }

        fun getNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack {
            return if (itemStack !is CraftItemStack) {
                CraftItemStack.asNMSCopy(itemStack)
            } else {
                field[itemStack] as net.minecraft.world.item.ItemStack
            }
        }
    }
}