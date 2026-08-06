package com.willfp.eco.core.anvil

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * Plugin-supplied rules for how the eco anvil shell merges and vetoes items.
 * The shell owns event handling, repair, durability, rename and cost; the
 * handler only decides enchant-specific behavior.
 */
interface AnvilHandler {
    /**
     * Whether [enchant] at [level] may be added to [target], given [existing] enchants.
     *
     * Only consulted for enchantments that are not already on the target; levels of
     * enchantments already present are merged with [maxLevel] instead.
     *
     * @param enchant  The enchantment being added.
     * @param level    The level being added.
     * @param target   The item the enchantment would be added to.
     * @param existing The enchantments already on the merge result.
     * @return If the enchantment may be added.
     */
    fun canCombine(
        enchant: Enchantment,
        level: Int,
        target: ItemStack,
        existing: Set<Enchantment>
    ): Boolean

    /**
     * The maximum level the shell should clamp [enchant] to when bumping.
     *
     * Defaults to [Enchantment.getMaxLevel].
     *
     * @param enchant The enchantment.
     * @return The maximum level.
     */
    fun maxLevel(enchant: Enchantment): Int = enchant.maxLevel

    /**
     * If true, the anvil produces no result (e.g. a curse blocking combination).
     *
     * Defaults to false, i.e. nothing is blocked.
     *
     * @param left  The item in the left anvil slot, or null if empty.
     * @param right The item in the right anvil slot, or null if empty.
     * @return If the anvil should be blocked.
     */
    fun isBlocked(left: ItemStack?, right: ItemStack?): Boolean = false
}
