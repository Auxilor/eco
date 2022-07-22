@file:JvmName("VectorUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.util.Vector

/** @see VectorUtils.isFinite */
val Vector.isFinite: Boolean
    get() = VectorUtils.isFinite(this)

/** @see VectorUtils.simplifyVector */
fun Vector.simplify(): Vector =
    VectorUtils.simplifyVector(this)

/** @see VectorUtils.isSafeVelocity */
val Vector.isSafeVelocity: Boolean
    get() = VectorUtils.isSafeVelocity(this)
