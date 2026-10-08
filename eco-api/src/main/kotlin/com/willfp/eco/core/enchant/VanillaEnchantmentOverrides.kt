package com.willfp.eco.core.enchant

import org.bukkit.NamespacedKey

/**
 * Overrides for vanilla enchantments, read live each time the value is needed.
 *
 * Only applied once the registry has been unfrozen with [CustomEnchantments.unfreezeRegistry].
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
