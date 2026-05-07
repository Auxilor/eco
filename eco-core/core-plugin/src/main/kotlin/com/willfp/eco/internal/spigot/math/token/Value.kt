package com.willfp.eco.internal.spigot.math.token

interface Value : Token {
    fun getValue(variableValues: DoubleArray): Double
    fun getClone(): Value
}