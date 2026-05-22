package com.willfp.eco.internal.spigot.math.token

data class UnaryOperation(
    val operator: UnaryOperator,
    val first: Value
) : Value {
    override fun getType() = TokenType.UNARY_OPERATION

    override fun getValue(variableValues: DoubleArray) =
        operator.getOperation().applyAsDouble(first.getValue(variableValues))

    override fun getClone() = UnaryOperation(operator, first.getClone())

    override fun toString() = "(${operator.getSymbol()}${first})"
}