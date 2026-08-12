package com.willfp.eco.internal.spigot.math.api

import com.willfp.eco.core.math.ExpressionEnvironment
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import com.willfp.eco.internal.spigot.math.functional.Function
import java.util.OptionalInt
import java.util.function.ToDoubleFunction

/**
 * Builds [EcoExpressionEnvironment]s. Mutable and single-threaded, as documented on
 * [ExpressionEnvironment.Builder].
 */
class EcoExpressionEnvironmentBuilder : ExpressionEnvironment.Builder {
    private var variableNames: Array<out String> = emptyArray()
    private val functions = ArrayList<CustomFunction>()

    override fun variables(vararg names: String): ExpressionEnvironment.Builder {
        variableNames = names
        return this
    }

    override fun function(
        name: String,
        arity: Int,
        function: ToDoubleFunction<DoubleArray>
    ): ExpressionEnvironment.Builder = function(name, arity, function, true)

    override fun function(
        name: String,
        function: ToDoubleFunction<DoubleArray>
    ): ExpressionEnvironment.Builder {
        functions.add(CustomFunction(name, OptionalInt.empty(), function, true))
        return this
    }

    override fun function(
        name: String,
        arity: Int,
        function: ToDoubleFunction<DoubleArray>,
        deterministic: Boolean
    ): ExpressionEnvironment.Builder {
        functions.add(CustomFunction(name, OptionalInt.of(arity), function, deterministic))
        return this
    }

    override fun build(): ExpressionEnvironment {
        val env = ExpressionEnv()

        val builtins = env.getFunctionNames()
        val declared = HashSet<String>()

        for (function in functions) {
            val name = function.name
            require(name.isNotEmpty()) { "Function name cannot be empty" }
            require(name !in builtins) {
                "Function name '$name' collides with a built-in function"
            }
            require(name !in ExpressionEnv.RESERVED_NAMES) {
                "Function name '$name' collides with an operator or constant"
            }
            require(name !in variableNames) {
                "Function name '$name' collides with a declared variable"
            }
            require(declared.add(name)) {
                "Function name '$name' is registered more than once"
            }
        }

        if (variableNames.isNotEmpty()) {
            env.setVariableNames(*variableNames)
        }

        for (function in functions) {
            env.addFunction(
                Function(
                    function.name,
                    function.arity,
                    deterministic = function.deterministic,
                    function = function.function
                )
            )
        }

        return EcoExpressionEnvironment(env)
    }

    private class CustomFunction(
        val name: String,
        val arity: OptionalInt,
        val function: ToDoubleFunction<DoubleArray>,
        val deterministic: Boolean
    )
}
