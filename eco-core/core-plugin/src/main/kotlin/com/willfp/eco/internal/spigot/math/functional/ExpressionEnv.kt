package com.willfp.eco.internal.spigot.math.functional

import com.willfp.eco.internal.spigot.math.Variable
import com.willfp.eco.internal.spigot.math.data.CharTree
import com.willfp.eco.internal.spigot.math.token.BinaryOperator
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.Token
import com.willfp.eco.internal.spigot.math.token.UnaryOperator
import com.willfp.eco.internal.spigot.math.token.Value
import com.willfp.eco.util.randDouble
import java.util.OptionalInt
import java.util.function.ToDoubleFunction
import kotlin.math.max
import kotlin.math.min

class ExpressionEnv {
    private val binaryOperators: CharTree<BinaryOperator> = CharTree()
    private val leadingOperators: CharTree<Token> = CharTree()
    private val values: CharTree<Value> = CharTree()

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
        addFunction(Function("min", OptionalInt.of(2)) { min(it[0], it[1]) })
        addFunction(Function("max", OptionalInt.of(2)) { max(it[0], it[1]) })
        addFunction(Function("random", OptionalInt.of(2)) { randDouble(it[0], it[1]) })
    }

    private fun checkName(name: String?) {
        if (name == null || name.isEmpty()) {
            throw IllegalArgumentException("Identifier cannot be empty or null")
        }
    }

    fun addFunction(function: Function): ExpressionEnv {
        val name = function.getName()
        checkName(name)
        leadingOperators.set(name, function)
        return this
    }

    fun addFunctions(vararg functions: Function): ExpressionEnv {
        for (function in functions) addFunction(function)
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
        addFunction(Function(name, OptionalInt.of(argCount), func))
        return this
    }

    fun addFunction(name: String, func: ToDoubleFunction<DoubleArray>): ExpressionEnv {
        addFunction(Function(name, OptionalInt.empty(), func))
        return this
    }

    fun getLeadingOperators(): CharTree<Token> = leadingOperators
    fun getBinaryOperators(): CharTree<BinaryOperator> = binaryOperators
    fun getValues(): CharTree<Value> = values
    fun getVariableCount(): Int = varCount
}
