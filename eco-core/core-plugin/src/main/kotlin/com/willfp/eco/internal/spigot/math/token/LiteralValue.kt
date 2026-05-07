package com.willfp.eco.internal.spigot.math.token

data class LiteralValue(val value: Double) : Value {
    override fun getType() = TokenType.LITERAL_VALUE
    override fun getValue(variableValues: DoubleArray) = value
    override fun getClone() = copy()
    override fun toString() = value.toString()
}