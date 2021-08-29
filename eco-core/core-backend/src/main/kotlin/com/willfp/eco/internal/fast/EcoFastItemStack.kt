package com.willfp.eco.internal.fast

import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

abstract class EcoFastItemStack<T: Any>(
    val handle: T,
    val bukkit: ItemStack
) : FastItemStack {
    override fun unwrap(): ItemStack {
        return bukkit
    }

    fun getBitModifier(hideFlag: ItemFlag): Int {
        return 1 shl hideFlag.ordinal
    }
}