package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.math.RandomSource
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The default source, used by every evaluation that does not supply one explicitly.
 * Preserves the pre-upgrade behaviour of drawing from [ThreadLocalRandom].
 */
object ThreadLocalRandomSource : RandomSource {
    override fun nextDouble(): Double = ThreadLocalRandom.current().nextDouble()
}

/**
 * Replicates `NumberUtils.randFloat` semantics against a pluggable [random]:
 * bounds are ordered internally, and bounds within `EPSILON` of each other collapse to [min].
 */
fun randomInRange(random: RandomSource, min: Double, max: Double): Double {
    if (abs(min - max) < RANDOM_EPSILON) {
        return min
    }
    val lower = min(min, max)
    val upper = max(min, max)
    return lower + random.nextDouble() * (upper - lower)
}

private const val RANDOM_EPSILON = 1e-6
