package com.willfp.eco.internal.fast

import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemStack

abstract class EcoFastItemStack<T: Any>(
    val handle: T,
    val bukkit: ItemStack
) : FastItemStack {

    companion object {
        init {
            ConfigurationSerialization.registerClass(FastItemStack::class.java);
        }
    }

    override fun unwrap(): ItemStack {
        return bukkit
    }
}
