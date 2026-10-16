package com.willfp.eco.core.enchant

import org.bukkit.NamespacedKey

/**
 * Overrides for vanilla enchantments.
 *
 * Applied to the registry when set with [CustomEnchantments.setVanillaOverrides]
 * and each time the registry is frozen with [CustomEnchantments.freezeRegistry].
 */
interface VanillaEnchantmentOverrides {
    /**
     * Get the max level for the vanilla enchantment with [key].
     *
     * @return The max level, or null to keep vanilla.
     */
    fun getMaxLevel(key: NamespacedKey): Int? = null

    /**
     * Get the conflicts for the vanilla enchantment with [key].
     *
     * @return The conflicts, or null to keep vanilla.
     */
    fun getConflicts(key: NamespacedKey): Collection<NamespacedKey>? = null
}
