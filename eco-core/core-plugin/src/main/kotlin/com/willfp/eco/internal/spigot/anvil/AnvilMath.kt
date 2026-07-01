package com.willfp.eco.internal.spigot.anvil

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/** Treat any value below 1 as effectively unlimited. */
fun Int.infiniteIfNegative() = if (this < 1) Int.MAX_VALUE else this

/** Vanilla-style prior-work penalty: (cost + 1) * 2 - 1. */
fun applyReworkPenalty(repairCost: Int): Int = (repairCost + 1) * 2 - 1

/** XP cost = enchantLevelDiff^costExponent + unitRepairCost, rounded. */
fun computeXpCost(enchantLevelDiff: Int, unitRepairCost: Int, costExponent: Double): Int =
    (enchantLevelDiff.toDouble().pow(costExponent) + unitRepairCost).roundToInt()

/**
 * Per-enchant merge rule for an enchant already present on the target.
 * Equal levels bump by one (capped at [maxLevel]); otherwise take the higher.
 */
fun mergeEnchantLevel(existing: Int, incoming: Int, maxLevel: Int): Int =
    if (incoming == existing) min(maxLevel, incoming + 1) else max(incoming, existing)
