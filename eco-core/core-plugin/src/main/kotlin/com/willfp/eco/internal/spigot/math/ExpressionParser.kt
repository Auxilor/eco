package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.data.FastNumberParsing
import com.willfp.eco.internal.spigot.math.exceptions.ExpressionCompilationException
import com.willfp.eco.internal.spigot.math.functional.ArgumentList
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import com.willfp.eco.internal.spigot.math.functional.Function
import com.willfp.eco.internal.spigot.math.functional.FunctionCall
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.LiteralValue
import com.willfp.eco.internal.spigot.math.token.Token
import com.willfp.eco.internal.spigot.math.token.UnaryOperation
import com.willfp.eco.internal.spigot.math.token.UnaryOperator
import com.willfp.eco.internal.spigot.math.token.Value
import java.util.OptionalInt

class ExpressionParser(val input: String, private val environment: ExpressionEnv) {
    var cursor: Int = 0

    fun peek() = input[cursor]
    fun advance() = input[cursor++]
    fun advanceCursor() { cursor++ }
    fun isAtEnd() = cursor >= input.length

    fun expectChar(c: Char) {
        if (isAtEnd() || advance() != c) {
            throw ExpressionCompilationException(this, "Expected '$c'")
        }
    }

    private fun error(msg: String): Nothing {
        throw ExpressionCompilationException(this, msg)
    }

    private fun whitespace(): Boolean {
        while (!isAtEnd() && input[cursor].isWhitespace()) cursor++
        return true
    }

    private fun parseExpression(): Value {
        if (isAtEnd()) error("Expected expression")
        val first = parseTerm()
        if (isAtEnd() || peek() == ')' || peek() == ',') return first
        val tokens = ShuntingYard()
        tokens.addValue(first)
        while (whitespace() && !isAtEnd() && peek() != ')' && peek() != ',') {
            val token = environment.getBinaryOperators().getWith(this)
                ?: error("Expected binary operator")
            tokens.addOperator(token)
            whitespace()
            tokens.addValue(parseTerm())
        }
        return tokens.finish()
    }

    private fun parseNestedExpression(): Value {
        expectChar('(')
        whitespace()
        val expression = parseExpression()
        expectChar(')')
        return expression
    }

    private fun parseTerm(): Value {
        return when (peek()) {
            in '0'..'9', '.' -> parseLiteral()
            '(' -> parseNestedExpression()
            else -> {
                val leadingOperator = environment.getLeadingOperators().getWith(this)
                if (leadingOperator != null) return parseLeadingOperation(leadingOperator)
                environment.getValues().getWith(this) ?: error("Expected value")
            }
        }
    }

    private fun parseLiteral(): LiteralValue {
        val start = cursor
        while (!isAtEnd()) {
            val c = peek()
            if (!c.isDigit() && c != '.') break
            advanceCursor()
        }
        return LiteralValue(FastNumberParsing.parseDouble(input, start, cursor))
    }

    private fun parseLeadingOperation(token: Token): Value {
        whitespace()
        return when (token) {
            is UnaryOperator -> {
                val term = parseTerm()
                if (token.isPure() && (term is LiteralValue || term is Constant)) {
                    LiteralValue(token.getOperation().applyAsDouble(term.getValue(EMPTY_VARS)))
                } else {
                    UnaryOperation(token, term)
                }
            }
            is Function -> {
                val args = parseArgumentList(token.getArgCount())
                FunctionCall(token, args.getArguments())
            }
            else -> error("Expected leading operation")
        }
    }

    private fun parseArgumentList(args: OptionalInt): ArgumentList {
        expectChar('(')
        whitespace()
        val initialCapacity = if (args.isPresent) args.asInt else 0
        val list = ArrayList<Value>(initialCapacity)
        if (peek() == ')') {
            if (args.isPresent && args.asInt != 0) {
                error("Expected ${args.asInt} arguments")
            }
            advance()
            return ArgumentList(emptyArray())
        }
        list.add(parseExpression())
        whitespace()
        while (peek() == ',') {
            advance()
            whitespace()
            list.add(parseExpression())
            whitespace()
        }
        if (args.isPresent && list.size != args.asInt) {
            error("Expected ${args.asInt} arguments but got ${list.size}")
        }
        expectChar(')')
        return ArgumentList(list.toTypedArray())
    }

    fun parse(): CompiledExpression {
        whitespace()
        val value = parseExpression()
        whitespace()
        if (!isAtEnd()) error("Dangling term")
        return CompiledExpression(value, environment.getVariableCount())
    }

    companion object {
        private val EMPTY_VARS = DoubleArray(0)
    }
}
