package com.willfp.eco.core.anvil

import org.bukkit.entity.Player

/**
 * Numeric / behavioral knobs for the anvil shell, supplied by the registering
 * plugin (eco itself stays config-agnostic).
 */
data class AnvilSettings(
    val costExponent: Double,
    /** Max enchants on an item; values below 1 mean unlimited. */
    val enchantLimit: Int,
    val useReworkPenalty: Boolean,
    val maxRepairCost: Int,
    val clampRepairCost: Boolean,
    /** Whether [Player] may use color codes in anvil rename text. */
    val colorNameAllowed: (Player) -> Boolean
)
