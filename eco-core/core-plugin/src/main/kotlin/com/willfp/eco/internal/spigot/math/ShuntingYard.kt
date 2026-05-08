package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.token.BinaryOperation
import com.willfp.eco.internal.spigot.math.token.BinaryOperator
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.LiteralValue
import com.willfp.eco.internal.spigot.math.token.Value

class ShuntingYard {
    private val operators = ArrayDeque<BinaryOperator>()
    private val stack = ArrayDeque<Value>()

    fun addOperator(operator: BinaryOperator) {
        while (operators.isNotEmpty() && shouldPopOperator(operator, operators.last())) {
            createOperation()
        }
        operators.addLast(operator)
    }

    private fun shouldPopOperator(incoming: BinaryOperator, top: BinaryOperator): Boolean =
        if (incoming.isRightAssociative()) {
            incoming.getPriority() < top.getPriority()
        } else {
            incoming.getPriority() <= top.getPriority()
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
