package com.willfp.eco.internal.spigot.proxy.v1_16_R3.fast

import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

private val field: Field = CraftItemStack::class.java.getDeclaredField("handle").apply {
    isAccessible = true
}

fun ItemStack.getNMSStack(): net.minecraft.server.v1_16_R3.ItemStack {
    return if (this !is CraftItemStack) {
        CraftItemStack.asNMSCopy(this)
    } else {
        field[this] as net.minecraft.server.v1_16_R3.ItemStack? ?: CraftItemStack.asNMSCopy(this)
    }
}