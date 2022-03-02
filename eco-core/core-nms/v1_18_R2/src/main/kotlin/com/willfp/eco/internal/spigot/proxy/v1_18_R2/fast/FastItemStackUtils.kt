package com.willfp.eco.internal.spigot.proxy.v1_18_R2.fast

import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

private val field: Field = CraftItemStack::class.java.getDeclaredField("handle").apply {
    isAccessible = true
}

fun ItemStack.getNMSStack(): net.minecraft.world.item.ItemStack {
    return if (this !is CraftItemStack) {
        CraftItemStack.asNMSCopy(this)
    } else {
        field[this] as net.minecraft.world.item.ItemStack? ?: CraftItemStack.asNMSCopy(this)
    }
}