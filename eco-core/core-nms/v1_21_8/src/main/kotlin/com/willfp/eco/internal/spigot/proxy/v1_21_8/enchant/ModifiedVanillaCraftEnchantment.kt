package com.willfp.eco.internal.spigot.proxy.v1_21_8.enchant

import com.willfp.eco.core.enchant.VanillaEnchantmentOverrides
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment as NMSEnchantment
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.enchantments.CraftEnchantment
import org.bukkit.enchantments.Enchantment

class ModifiedVanillaCraftEnchantment(
    private val enchantmentKey: NamespacedKey,
    holder: Holder<NMSEnchantment>,
    private val overrides: () -> VanillaEnchantmentOverrides?
) : CraftEnchantment(holder) {
    private val conflicts: Collection<NamespacedKey>?
        get() = overrides()?.getConflicts(enchantmentKey)

    override fun getMaxLevel(): Int = overrides()?.getMaxLevel(enchantmentKey) ?: super.getMaxLevel()

    override fun conflictsWith(other: Enchantment): Boolean {
        val otherConflicts = when (other) {
            is ModifiedVanillaCraftEnchantment -> other.conflicts?.contains(this.enchantmentKey) == true
            else -> other.conflictsWith(this)
        }

        return this.conflicts?.contains(other.key) ?: super.conflictsWith(other)
                || otherConflicts
    }
}
