package com.willfp.eco.internal.spigot.math.functional

/**
 * Marks a [Function] that the bytecode compiler lowers to a dedicated VM opcode rather than a
 * host call through `OP_CALL`.
 *
 * Used for built-ins that need machinery the [java.util.function.ToDoubleFunction] contract cannot
 * express — currently, access to the evaluation's `RandomSource`.
 */
enum class BuiltinOpcode {
    /** `random(min, max)` — two arguments, drawn from the evaluation's random source. */
    RANDOM_RANGE
}
