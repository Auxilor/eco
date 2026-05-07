package redempt.crunch

import redempt.crunch.token.BinaryOperation
import redempt.crunch.token.BinaryOperator
import redempt.crunch.token.Constant
import redempt.crunch.token.LiteralValue
import redempt.crunch.token.Value

class ShuntingYard {
    private val operators = ArrayDeque<BinaryOperator>()
    private val stack = ArrayDeque<Value>()

    fun addOperator(operator: BinaryOperator) {
        while (operators.isNotEmpty() && operator.getPriority() <= operators.last().getPriority()) {
            createOperation()
        }
        operators.addLast(operator)
    }

    fun addValue(value: Value) {
        stack.addLast(value)
    }

    private fun createOperation() {
        val op = operators.removeLast()
        val right = stack.removeLast()
        val left = stack.removeLast()
        if (isCompileTimeConstant(right) && isCompileTimeConstant(left)) {
            stack.addLast(LiteralValue(op.getOperation().applyAsDouble(left.getValue(EMPTY_VARS), right.getValue(EMPTY_VARS))))
        } else {
            stack.addLast(BinaryOperation(op, left, right))
        }
    }

    fun finish(): Value {
        while (stack.size > 1) createOperation()
        return stack.removeLast()
    }

    companion object {
        private val EMPTY_VARS = DoubleArray(0)

        private fun isCompileTimeConstant(value: Value): Boolean =
            value is LiteralValue || value is Constant
    }
}
