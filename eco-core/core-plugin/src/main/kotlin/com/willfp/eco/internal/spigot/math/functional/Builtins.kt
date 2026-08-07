package com.willfp.eco.internal.spigot.math.functional

import com.willfp.eco.util.randDouble
import java.util.OptionalInt
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.truncate

/**
 * Registers every default function into [env].
 *
 * Called from [ExpressionEnv]'s constructor, so both the placeholder-driven `NumberUtils` path and
 * the public expression environment API gain these.
 */
internal fun registerBuiltins(env: ExpressionEnv) {
    // Variadic reductions. At least one argument required; zero is a compilation error.
    env.addFunction(Function("min", OptionalInt.empty(), minArgCount = 1) { args ->
        var m = args[0]
        for (i in 1 until args.size) m = min(m, args[i])
        m
    })
    env.addFunction(Function("max", OptionalInt.empty(), minArgCount = 1) { args ->
        var m = args[0]
        for (i in 1 until args.size) m = max(m, args[i])
        m
    })

    // Randomness. Lowered to OP_RAND2; the lambda is the tree-walk fallback only.
    env.addFunction(
        Function(
            "random",
            OptionalInt.of(2),
            deterministic = false,
            minArgCount = 2,
            opcode = BuiltinOpcode.RANDOM_RANGE
        ) { randDouble(it[0], it[1]) }
    )

    // Shaping
    env.addFunction(Function("clamp", OptionalInt.of(3), minArgCount = 3) {
        max(it[1], min(it[2], it[0]))
    })
    env.addFunction(Function("lerp", OptionalInt.of(3), minArgCount = 3) {
        it[0] + (it[1] - it[0]) * it[2]
    })
    env.addFunction(Function("smoothstep", OptionalInt.of(3), minArgCount = 3) {
        val t = max(0.0, min(1.0, (it[2] - it[0]) / (it[1] - it[0])))
        t * t * (3.0 - 2.0 * t)
    })
    env.addFunction(Function("remap", OptionalInt.of(5), minArgCount = 5) {
        it[3] + (it[0] - it[1]) * (it[4] - it[3]) / (it[2] - it[1])
    })

    // Core maths
    env.addFunction(Function("exp", OptionalInt.of(1), minArgCount = 1) { exp(it[0]) })
    env.addFunction(Function("log10", OptionalInt.of(1), minArgCount = 1) { log10(it[0]) })
    env.addFunction(Function("log2", OptionalInt.of(1), minArgCount = 1) { ln(it[0]) / LN_2 })
    env.addFunction(Function("sign", OptionalInt.of(1), minArgCount = 1) { sign(it[0]) })
    env.addFunction(Function("trunc", OptionalInt.of(1), minArgCount = 1) { truncate(it[0]) })
    env.addFunction(Function("atan2", OptionalInt.of(2), minArgCount = 2) { atan2(it[0], it[1]) })
    env.addFunction(Function("hypot", OptionalInt.of(2), minArgCount = 2) { hypot(it[0], it[1]) })
    env.addFunction(Function("pow", OptionalInt.of(2), minArgCount = 2) { Math.pow(it[0], it[1]) })

    // Conditionals. `if` evaluates all three arguments eagerly - arguments are emitted before the
    // call, so there is no short-circuit - and treats any non-zero condition as true.
    env.addFunction(Function("if", OptionalInt.of(3), minArgCount = 3) {
        if (it[0] != 0.0) it[1] else it[2]
    })
    env.addFunction(Function("step", OptionalInt.of(2), minArgCount = 2) {
        if (it[1] < it[0]) 0.0 else 1.0
    })
}

private val LN_2 = ln(2.0)
