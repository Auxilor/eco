package com.willfp.eco.internal.spigot.math.functional

import com.willfp.eco.internal.spigot.math.Variable
import com.willfp.eco.internal.spigot.math.data.CharTree
import com.willfp.eco.internal.spigot.math.token.BinaryOperator
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.Token
import com.willfp.eco.internal.spigot.math.token.UnaryOperator
import com.willfp.eco.internal.spigot.math.token.Value
import java.util.OptionalInt
import java.util.function.ToDoubleFunction

class ExpressionEnv {
    private val binaryOperators: CharTree<BinaryOperator> = CharTree()
    private val leadingOperators: CharTree<Token> = CharTree()
    private val values: CharTree<Value> = CharTree()
    private val functionNames: MutableSet<String> = LinkedHashSet()

    private var varCount = 0

    init {
        for (operator in BinaryOperator.entries) {
            binaryOperators.set(operator.getSymbol(), operator)
        }
        for (operator in UnaryOperator.entries) {
            leadingOperators.set(operator.getSymbol(), operator)
        }
        for (constant in Constant.entries) {
            values.set(constant.toString(), constant)
        }
        registerBuiltins(this)
    }

    private fun checkName(name: String?) {
        if (name.isNullOrEmpty()) {
            throw IllegalArgumentException("Identifier cannot be empty or null")
        }
    }

    fun addFunction(function: Function): ExpressionEnv {
        val name = function.getName()
        checkName(name)
        leadingOperators.set(name, function)
        functionNames.add(name)
        return this
    }

    fun setVariableNames(vararg names: String): ExpressionEnv {
        varCount = names.size
        for (i in names.indices) {
            checkName(names[i])
            values.set(names[i], Variable(i))
        }
        return this
    }

    fun addFunction(name: String, argCount: Int, func: ToDoubleFunction<DoubleArray>): ExpressionEnv {
        addFunction(Function(name, OptionalInt.of(argCount), function = func))
        return this
    }

    fun addFunction(name: String, func: ToDoubleFunction<DoubleArray>): ExpressionEnv {
        addFunction(Function(name, OptionalInt.empty(), function = func))
        return this
    }

    fun getLeadingOperators(): CharTree<Token> = leadingOperators
    fun getBinaryOperators(): CharTree<BinaryOperator> = binaryOperators
    fun getValues(): CharTree<Value> = values
    fun getVariableCount(): Int = varCount

    /** Every function name currently registered, built-in or custom. */
    fun getFunctionNames(): Set<String> = functionNames

    companion object {
        /**
         * Names that can never be used for a custom function: every unary operator symbol and every
         * constant. Built-in function names are not listed here — read them from a fresh
         * [ExpressionEnv] with [getFunctionNames].
         */
        val RESERVED_NAMES: Set<String> = buildSet {
            for (operator in UnaryOperator.entries) add(operator.getSymbol())
            for (constant in Constant.entries) add(constant.toString())
        }
    }
}
