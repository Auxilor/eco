package com.willfp.eco.internal.particle

/**
 * Coerce a possibly-NaN-or-infinite double into a finite value safe for Bukkit.
 * NaN → 0.0, ±Inf → ±Double.MAX_VALUE/2. Most Bukkit calls handle large finite
 * values gracefully; NaN crashes some particle types.
 */
internal fun sanitiseDouble(d: Double): Double = when {
    d.isNaN() -> 0.0
    d == Double.POSITIVE_INFINITY -> Double.MAX_VALUE / 2
    d == Double.NEGATIVE_INFINITY -> -Double.MAX_VALUE / 2
    else -> d
}

/**
 * Coerce to a non-negative int suitable for Bukkit `count` arguments. Caps to
 * a sane upper bound to prevent runaway configs from melting servers.
 */
internal fun sanitiseCount(d: Double): Int {
    val clamped = sanitiseDouble(d).coerceIn(0.0, 10_000.0)
    return clamped.toInt()
}