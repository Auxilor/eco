package com.willfp.eco.internal.spigot.math.token

import java.util.Locale

enum class Constant(private val value: Double) : Value {
    PI(Math.PI),
    E(Math.E),
    TRUE(1.0),
    FALSE(0.0);

    private val lowerName: String = name.lowercase(Locale.ROOT)

    override fun getType() = TokenType.LITERAL_VALUE
    override fun getValue(variableValues: DoubleArray) = value
    override fun getClone(): Value = this
    override fun toString() = lowerName
}