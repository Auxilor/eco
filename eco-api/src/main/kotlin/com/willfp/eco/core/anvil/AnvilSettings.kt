package com.willfp.eco.core.anvil

import org.bukkit.entity.Player

/**
 * Numeric / behavioral knobs for the anvil shell, supplied by the registering
 * plugin (eco itself stays config-agnostic).
 */
data class AnvilSettings(
    /** Exponent applied to the merged enchantment level difference when computing the XP cost. */
    val costExponent: Double,
    /** Max enchants on an item; values below 1 mean unlimited. */
    val enchantLimit: Int,
    /** Whether the vanilla rework (prior work) penalty is applied to the result. */
    val useReworkPenalty: Boolean,
    /** The maximum repair cost, used as the anvil's maximum repair cost and as the clamp ceiling. */
    val maxRepairCost: Int,
    /** If true, costs above [maxRepairCost] are clamped; if false, they are rejected instead. */
    val clampRepairCost: Boolean,
    /** Whether [Player] may use color codes in anvil rename text. */
    val colorNameAllowed: (Player) -> Boolean
)
