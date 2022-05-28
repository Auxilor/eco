@file:JvmName("NumberUtilsExtensions")

package com.willfp.eco.util

/** @see NumberUtils.toNumeral */
fun Number.toNumeral(): String =
    NumberUtils.toNumeral(this.toInt())
