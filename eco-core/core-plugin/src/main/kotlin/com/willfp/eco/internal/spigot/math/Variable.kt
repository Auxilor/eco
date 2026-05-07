package redempt.crunch

import redempt.crunch.token.TokenType
import redempt.crunch.token.Value

data class Variable(val index: Int) : Value {
    override fun getType() = TokenType.VARIABLE
    override fun getValue(variableValues: DoubleArray) = variableValues[index]
    override fun getClone() = Variable(index)
    override fun toString() = "\$${index + 1}"
}