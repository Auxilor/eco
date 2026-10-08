package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.enchant.CustomEnchantment
import com.willfp.eco.core.enchant.VanillaEnchantmentOverrides
import org.bukkit.enchantments.Enchantment

interface EnchantmentRegistryProxy {
    fun unfreeze()

    fun freeze()

    fun register(enchantment: CustomEnchantment): Enchantment

    fun unregister(enchantment: CustomEnchantment)

    fun setVanillaOverrides(overrides: VanillaEnchantmentOverrides?)

    fun getCustomEnchantment(enchantment: Enchantment): CustomEnchantment?
}
