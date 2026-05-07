package redempt.crunch.token

data class BinaryOperation(
    val operator: BinaryOperator,
    val first: Value,
    val second: Value
) : Value {
    override fun getType() = TokenType.BINARY_OPERATION

    fun getValues() = arrayOf(first, second)

    override fun getValue(variableValues: DoubleArray) =
        operator.getOperation().applyAsDouble(first.getValue(variableValues), second.getValue(variableValues))

    override fun getClone() = BinaryOperation(operator, first.getClone(), second.getClone())

    override fun toString() = "(${first}${operator.getSymbol()}${second})"
}