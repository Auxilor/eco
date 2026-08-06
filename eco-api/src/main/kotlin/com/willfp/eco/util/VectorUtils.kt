@file:JvmName("VectorUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.util.Vector

/**
 * If all three components of this vector are finite, i.e. none of them is NaN or infinite.
 *
 * @see VectorUtils.isFinite
 */
val Vector.isFinite: Boolean
    get() = VectorUtils.isFinite(this)

/**
 * Reduce this vector to a unit vector along a single axis, keeping only its largest component.
 *
 * For example, `(-0.8, 0.01, -0.2)` becomes `(-1, 0, 0)`. Intended for normalised vectors: if
 * the x or z component has an absolute value greater than 1, the y axis is chosen instead.
 *
 * @return A new vector with one component set to 1 or -1 and the other two set to 0.
 * @see VectorUtils.simplifyVector
 */
fun Vector.simplify(): Vector =
    VectorUtils.simplifyVector(this)

/**
 * If this vector is safe to apply as an entity velocity, i.e. the absolute value of each of
 * its three components is less than 4.
 *
 * @see VectorUtils.isSafeVelocity
 */
val Vector.isSafeVelocity: Boolean
    get() = VectorUtils.isSafeVelocity(this)
