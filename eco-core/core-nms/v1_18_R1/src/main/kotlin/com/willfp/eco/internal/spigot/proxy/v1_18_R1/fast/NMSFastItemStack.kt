package com.willfp.eco.internal.spigot.proxy.v1_18_R1.fast

import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class NMSFastItemStack(private val itemStack: ItemStack): FastItemStack {
    override fun getEnchantmentsOnItem(checkStored: Boolean): MutableMap<Enchantment, Int> {
        val meta = itemStack.itemMeta ?: return mutableMapOf()
        val enchants = mutableMapOf<Enchantment, Int>()

        enchants.putAll(meta.enchants)
        if (checkStored && meta is EnchantmentStorageMeta) {
            enchants.putAll(meta.storedEnchants)
        }

        return enchants
    }

    override fun getLevelOnItem(enchantment: Enchantment, checkStored: Boolean): Int {
        return getEnchantmentsOnItem(checkStored)[enchantment] ?: 0
    }

    override fun setLore(lore: MutableList<String>?) {
        itemStack.itemMeta = itemStack.itemMeta.apply {
            this?.lore = lore
        }
    }

    override fun getLore(): MutableList<String> {
        return itemStack.itemMeta?.lore ?: mutableListOf()
    }

    override fun setRepairCost(cost: Int) {
        // Not implemented
    }

    override fun getRepairCost(): Int {
        // Not implemented
        return 0
    }

    override fun addItemFlags(vararg hideFlags: ItemFlag) {
        itemStack.itemMeta = itemStack.itemMeta.apply {
            this?.addItemFlags(*hideFlags)
        }
    }

    override fun removeItemFlags(vararg hideFlags: ItemFlag) {
        itemStack.itemMeta = itemStack.itemMeta.apply {
            this?.removeItemFlags(*hideFlags)
        }
    }

    override fun getItemFlags(): MutableSet<ItemFlag> {
        return itemStack.itemMeta?.itemFlags ?: mutableSetOf()
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        return itemStack.itemMeta?.hasItemFlag(flag) ?: false
    }

    override fun unwrap(): ItemStack {
        return itemStack
    }
}