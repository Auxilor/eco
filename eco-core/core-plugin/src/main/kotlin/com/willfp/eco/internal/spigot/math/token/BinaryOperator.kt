package com.willfp.eco.internal.spigot.math.token

import java.util.function.DoubleBinaryOperator
import kotlin.math.pow

enum class BinaryOperator(
    private val symbol: String,
    private val priority: Int,
    private val operation: DoubleBinaryOperator
) : Token {
    BOOLEAN_OR("|", 0, DoubleBinaryOperator { a, b -> if (a == 1.0 || b == 1.0) 1.0 else 0.0 }),
    BOOLEAN_OR_ALT("||", 0, DoubleBinaryOperator { a, b -> if (a == 1.0 || b == 1.0) 1.0 else 0.0 }),
    BOOLEAN_AND("&", 0, DoubleBinaryOperator { a, b -> if (a == 1.0 && b == 1.0) 1.0 else 0.0 }),
    BOOLEAN_AND_ALT("&&", 0, DoubleBinaryOperator { a, b -> if (a == 1.0 && b == 1.0) 1.0 else 0.0 }),
    GREATER_THAN(">", 1, DoubleBinaryOperator { a, b -> if (a > b) 1.0 else 0.0 }),
    LESS_THAN("<", 1, DoubleBinaryOperator { a, b -> if (a < b) 1.0 else 0.0 }),
    EQUAL_TO("=", 1, DoubleBinaryOperator { a, b -> if (a == b) 1.0 else 0.0 }),
    EQUAL_TO_ALT("==", 1, DoubleBinaryOperator { a, b -> if (a == b) 1.0 else 0.0 }),
    NOT_EQUAL_TO("!=", 1, DoubleBinaryOperator { a, b -> if (a != b) 1.0 else 0.0 }),
    GREATER_THAN_OR_EQUAL_TO(">=", 1, DoubleBinaryOperator { a, b -> if (a >= b) 1.0 else 0.0 }),
    LESS_THAN_OR_EQUAL_TO("<=", 1, DoubleBinaryOperator { a, b -> if (a <= b) 1.0 else 0.0 }),
    EXPONENT("^", 5, DoubleBinaryOperator { a, b -> a.pow(b) }),
    MULTIPLY("*", 4, DoubleBinaryOperator { a, b -> a * b }),
    DIVIDE("/", 4, DoubleBinaryOperator { a, b -> a / b }),
    MODULUS("%", 4, DoubleBinaryOperator { a, b -> a % b }),
    ADD("+", 3, DoubleBinaryOperator { a, b -> a + b }),
    SUBTRACT("-", 3, DoubleBinaryOperator { a, b -> a - b }),
    SCIENTIFIC_NOTATION("E", 5, DoubleBinaryOperator { a, b -> a * 10.0.pow(b) });

    override fun getType() = TokenType.BINARY_OPERATOR
    fun getSymbol() = symbol
    fun getOperation() = operation
    fun getPriority() = priority
    override fun toString() = symbol
}