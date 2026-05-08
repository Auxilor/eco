package com.willfp.eco.internal.spigot.math.token

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleUnaryOperator
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

enum class UnaryOperator(
    private val symbol: String,
    private val operation: DoubleUnaryOperator,
    private val pure: Boolean
) : Token {
    NEGATE("-", DoubleUnaryOperator { d -> -d }),
    NOT("!", DoubleUnaryOperator { d -> if (d == 1.0) 0.0 else 1.0 }),
    SIN("sin", DoubleUnaryOperator { d -> sin(d) }),
    COS("cos", DoubleUnaryOperator { d -> cos(d) }),
    TAN("tan", DoubleUnaryOperator { d -> tan(d) }),
    SINH("sinh", DoubleUnaryOperator { d -> sinh(d) }),
    COSH("cosh", DoubleUnaryOperator { d -> cosh(d) }),
    TANH("tanh", DoubleUnaryOperator { d -> tanh(d) }),
    ASIN("asin", DoubleUnaryOperator { d -> asin(d) }),
    ACOS("acos", DoubleUnaryOperator { d -> acos(d) }),
    ATAN("atan", DoubleUnaryOperator { d -> atan(d) }),
    ABS("abs", DoubleUnaryOperator { d -> abs(d) }),
    ROUND("round", DoubleUnaryOperator { d -> Math.round(d).toDouble() }),
    FLOOR("floor", DoubleUnaryOperator { d -> floor(d) }),
    CEIL("ceil", DoubleUnaryOperator { d -> ceil(d) }),
    LOG("log", DoubleUnaryOperator { d -> ln(d) }),
    SQRT("sqrt", DoubleUnaryOperator { d -> sqrt(d) }),
    CBRT("cbrt", DoubleUnaryOperator { d -> Math.cbrt(d) }),
    RAND("rand", DoubleUnaryOperator { d -> ThreadLocalRandom.current().nextDouble() * d }, false);

    constructor(symbol: String, operation: DoubleUnaryOperator) : this(symbol, operation, true)

    override fun getType() = TokenType.UNARY_OPERATOR
    fun getOperation() = operation
    fun getPriority() = 6
    fun isPure() = pure
    fun getSymbol() = symbol
    override fun toString() = symbol
}