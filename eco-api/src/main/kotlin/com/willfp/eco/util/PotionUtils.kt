@file:JvmName("PotionUtilsExtensions")

package com.willfp.eco.util


/** @see PotionUtils.getDuration */
@Suppress("DEPRECATION", "REMOVAL")
@Deprecated("PotionData is marked for removal", level = DeprecationLevel.ERROR)
val org.bukkit.potion.PotionData.duration: Int
    get() = PotionUtils.getDuration(this)
