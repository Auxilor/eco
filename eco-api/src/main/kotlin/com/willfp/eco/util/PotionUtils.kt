@file:JvmName("PotionUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.potion.PotionData

/** @see PotionData.duration */
val PotionData.duration: Int
    get() = PotionUtils.getDuration(this)
