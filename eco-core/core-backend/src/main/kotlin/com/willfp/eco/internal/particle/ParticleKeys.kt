package com.willfp.eco.internal.particle

import org.bukkit.Color

/**
 * Parse a colour from a hex string, e.g. `00ff00` or `#00ff00`.
 *
 * @return The colour, or null if the string is not a valid hex colour.
 */
internal fun String.toParticleColor(): Color? {
    val hex = this.removePrefix("#").toIntOrNull(16) ?: return null

    return try {
        Color.fromRGB(hex)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Parse a particle size, where null means the size was not specified.
 *
 * @return The size, defaulting to 1.0, or null if the string is not a valid size.
 */
internal fun String?.toParticleSize(): Float? {
    this ?: return 1.0f

    return this.toFloatOrNull()?.coerceAtLeast(0.01f)
}
