@file:JvmName("PotionUtilsExtensions")

package com.willfp.eco.util


/** @see PotionUtils.getDuration */
@Suppress("DEPRECATION")
val org.bukkit.potion.PotionData.duration: Int
    get() = PotionUtils.getDuration(this)
