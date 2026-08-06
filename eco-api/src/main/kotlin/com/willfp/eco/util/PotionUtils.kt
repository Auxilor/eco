@file:JvmName("PotionUtilsExtensions")

package com.willfp.eco.util


/**
 * The duration of this potion data, in ticks.
 *
 * This is a stub that always returns 1: `PotionData` is marked for removal and is no longer
 * supported, so use the potion effect's own duration instead.
 *
 * @see PotionUtils.getDuration
 */
@Suppress("DEPRECATION", "REMOVAL")
@Deprecated("PotionData is marked for removal", level = DeprecationLevel.ERROR)
val org.bukkit.potion.PotionData.duration: Int
    get() = PotionUtils.getDuration(this)
