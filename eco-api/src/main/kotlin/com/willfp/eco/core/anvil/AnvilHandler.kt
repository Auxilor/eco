package com.willfp.eco.core.anvil

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * Plugin-supplied rules for how the eco anvil shell merges and vetoes items.
 * The shell owns event handling, repair, durability, rename and cost; the
 * handler only decides enchant-specific behavior.
 */
interface AnvilHandler {
    /** Whether [enchant] at [level] may be added to [target], given [existing] enchants. */
    fun canCombine(
        enchant: Enchantment,
        level: Int,
        target: ItemStack,
        existing: Set<Enchantment>
    ): Boolean

    /** The maximum level the shell should clamp [enchant] to when bumping. */
    fun maxLevel(enchant: Enchantment): Int = enchant.maxLevel

    /** If true, the anvil produces no result (e.g. a curse blocking combination). */
    fun isBlocked(left: ItemStack?, right: ItemStack?): Boolean = false
}
