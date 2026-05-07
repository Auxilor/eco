package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.functional.Function
import com.willfp.eco.internal.spigot.math.functional.FunctionCall
import com.willfp.eco.internal.spigot.math.token.BinaryOperation
import com.willfp.eco.internal.spigot.math.token.BinaryOperator
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.LiteralValue
import com.willfp.eco.internal.spigot.math.token.UnaryOperation
import com.willfp.eco.internal.spigot.math.token.UnaryOperator
import com.willfp.eco.internal.spigot.math.token.Value
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

class BytecodeExpression private constructor(
    private val opcodes: IntArray,
    private val constants: DoubleArray,
    private val functions: Array<Function>,
    private val stackSize: Int
) {
    private val stack = ThreadLocal.withInitial { DoubleArray(stackSize) }

    fun evaluate(vars: DoubleArray): Double {
        val stack = this.stack.get()
        val opcodes = this.opcodes
        val constants = this.constants
        var sp = -1
        var ip = 0

        while (ip < opcodes.size) {
            when (opcodes[ip++]) {
                OP_CONST -> stack[++sp] = constants[opcodes[ip++]]
                OP_VAR -> stack[++sp] = vars[opcodes[ip++]]
                OP_ADD -> { sp--; stack[sp] = stack[sp] + stack[sp + 1] }
                OP_SUB -> { sp--; stack[sp] = stack[sp] - stack[sp + 1] }
                OP_MUL -> { sp--; stack[sp] = stack[sp] * stack[sp + 1] }
                OP_DIV -> { sp--; stack[sp] = stack[sp] / stack[sp + 1] }
                OP_MOD -> { sp--; stack[sp] = stack[sp] % stack[sp + 1] }
                OP_POW -> { sp--; stack[sp] = stack[sp].pow(stack[sp + 1]) }
                OP_SCI_NOT -> { sp--; stack[sp] = stack[sp] * 10.0.pow(stack[sp + 1]) }
                OP_NEG -> stack[sp] = -stack[sp]
                OP_NOT -> stack[sp] = if (stack[sp] == 1.0) 0.0 else 1.0
                OP_SIN -> stack[sp] = sin(stack[sp])
                OP_COS -> stack[sp] = cos(stack[sp])
                OP_TAN -> stack[sp] = tan(stack[sp])
                OP_SINH -> stack[sp] = sinh(stack[sp])
                OP_COSH -> stack[sp] = cosh(stack[sp])
                OP_TANH -> stack[sp] = tanh(stack[sp])
                OP_ASIN -> stack[sp] = asin(stack[sp])
                OP_ACOS -> stack[sp] = acos(stack[sp])
                OP_ATAN -> stack[sp] = atan(stack[sp])
                OP_ABS -> stack[sp] = abs(stack[sp])
                OP_ROUND -> stack[sp] = Math.round(stack[sp]).toDouble()
                OP_FLOOR -> stack[sp] = floor(stack[sp])
                OP_CEIL -> stack[sp] = ceil(stack[sp])
                OP_LOG -> stack[sp] = ln(stack[sp])
                OP_SQRT -> stack[sp] = sqrt(stack[sp])
                OP_CBRT -> stack[sp] = Math.cbrt(stack[sp])
                OP_RAND -> stack[sp] = ThreadLocalRandom.current().nextDouble() * stack[sp]
                OP_GT -> { sp--; stack[sp] = if (stack[sp] > stack[sp + 1]) 1.0 else 0.0 }
                OP_LT -> { sp--; stack[sp] = if (stack[sp] < stack[sp + 1]) 1.0 else 0.0 }
                OP_EQ -> { sp--; stack[sp] = if (stack[sp] == stack[sp + 1]) 1.0 else 0.0 }
                OP_NEQ -> { sp--; stack[sp] = if (stack[sp] != stack[sp + 1]) 1.0 else 0.0 }
                OP_GTE -> { sp--; stack[sp] = if (stack[sp] >= stack[sp + 1]) 1.0 else 0.0 }
                OP_LTE -> { sp--; stack[sp] = if (stack[sp] <= stack[sp + 1]) 1.0 else 0.0 }
                OP_AND -> { sp--; stack[sp] = if (stack[sp] == 1.0 && stack[sp + 1] == 1.0) 1.0 else 0.0 }
                OP_OR -> { sp--; stack[sp] = if (stack[sp] == 1.0 || stack[sp + 1] == 1.0) 1.0 else 0.0 }
                OP_CALL -> {
                    val funcIdx = opcodes[ip++]
                    val argCount = opcodes[ip++]
                    val args = DoubleArray(argCount)
                    sp -= argCount
                    for (i in 0 until argCount) {
                        args[i] = stack[sp + 1 + i]
                    }
                    stack[++sp] = functions[funcIdx].call(args)
                }
            }
        }
        return stack[0]
    }

    companion object {
        private const val OP_CONST = 0
        private const val OP_VAR = 1
        private const val OP_ADD = 2
        private const val OP_SUB = 3
        private const val OP_MUL = 4
        private const val OP_DIV = 5
        private const val OP_MOD = 6
        private const val OP_POW = 7
        private const val OP_SCI_NOT = 8
        private const val OP_NEG = 9
        private const val OP_NOT = 10
        private const val OP_SIN = 11
        private const val OP_COS = 12
        private const val OP_TAN = 13
        private const val OP_SINH = 14
        private const val OP_COSH = 15
        private const val OP_TANH = 16
        private const val OP_ASIN = 17
        private const val OP_ACOS = 18
        private const val OP_ATAN = 19
        private const val OP_ABS = 20
        private const val OP_ROUND = 21
        private const val OP_FLOOR = 22
        private const val OP_CEIL = 23
        private const val OP_LOG = 24
        private const val OP_SQRT = 25
        private const val OP_CBRT = 26
        private const val OP_RAND = 27
        private const val OP_GT = 28
        private const val OP_LT = 29
        private const val OP_EQ = 30
        private const val OP_NEQ = 31
        private const val OP_GTE = 32
        private const val OP_LTE = 33
        private const val OP_AND = 34
        private const val OP_OR = 35
        private const val OP_CALL = 36

        private val EMPTY_VARS = DoubleArray(0)

        fun compile(value: Value): BytecodeExpression {
            val opcodes = ArrayList<Int>(16)
            val constants = ArrayList<Double>(8)
            val functions = ArrayList<Function>()
            var sp = 0
            var maxSp = 0

            fun pushSp() {
                sp++
                if (sp > maxSp) maxSp = sp
            }

            fun emitValue(v: Value) {
                when (v) {
                    is LiteralValue -> {
                        opcodes.add(OP_CONST)
                        opcodes.add(constants.size)
                        constants.add(v.value)
                        pushSp()
                    }
                    is Constant -> {
                        opcodes.add(OP_CONST)
                        opcodes.add(constants.size)
                        constants.add(v.getValue(EMPTY_VARS))
                        pushSp()
                    }
                    is Variable -> {
                        opcodes.add(OP_VAR)
                        opcodes.add(v.index)
                        pushSp()
                    }
                    is BinaryOperation -> {
                        emitValue(v.first)
                        emitValue(v.second)
                        opcodes.add(binaryOpcode(v.operator))
                        sp--
                    }
                    is UnaryOperation -> {
                        emitValue(v.first)
                        opcodes.add(unaryOpcode(v.operator))
                    }
                    is FunctionCall -> {
                        val vals = v.getValues()
                        for (arg in vals) {
                            emitValue(arg)
                        }
                        opcodes.add(OP_CALL)
                        opcodes.add(functions.size)
                        opcodes.add(vals.size)
                        functions.add(v.getFunction())
                        sp -= vals.size
                        pushSp()
                    }
                }
            }

            emitValue(value)
            return BytecodeExpression(
                opcodes.toIntArray(),
                constants.toDoubleArray(),
                functions.toTypedArray(),
                maxSp
            )
        }

        private fun binaryOpcode(op: BinaryOperator): Int = when (op) {
            BinaryOperator.ADD -> OP_ADD
            BinaryOperator.SUBTRACT -> OP_SUB
            BinaryOperator.MULTIPLY -> OP_MUL
            BinaryOperator.DIVIDE -> OP_DIV
            BinaryOperator.MODULUS -> OP_MOD
            BinaryOperator.EXPONENT -> OP_POW
            BinaryOperator.SCIENTIFIC_NOTATION -> OP_SCI_NOT
            BinaryOperator.GREATER_THAN -> OP_GT
            BinaryOperator.LESS_THAN -> OP_LT
            BinaryOperator.EQUAL_TO -> OP_EQ
            BinaryOperator.EQUAL_TO_ALT -> OP_EQ
            BinaryOperator.NOT_EQUAL_TO -> OP_NEQ
            BinaryOperator.GREATER_THAN_OR_EQUAL_TO -> OP_GTE
            BinaryOperator.LESS_THAN_OR_EQUAL_TO -> OP_LTE
            BinaryOperator.BOOLEAN_AND -> OP_AND
            BinaryOperator.BOOLEAN_AND_ALT -> OP_AND
            BinaryOperator.BOOLEAN_OR -> OP_OR
            BinaryOperator.BOOLEAN_OR_ALT -> OP_OR
        }

        private fun unaryOpcode(op: UnaryOperator): Int = when (op) {
            UnaryOperator.NEGATE -> OP_NEG
            UnaryOperator.NOT -> OP_NOT
            UnaryOperator.SIN -> OP_SIN
            UnaryOperator.COS -> OP_COS
            UnaryOperator.TAN -> OP_TAN
            UnaryOperator.SINH -> OP_SINH
            UnaryOperator.COSH -> OP_COSH
            UnaryOperator.TANH -> OP_TANH
            UnaryOperator.ASIN -> OP_ASIN
            UnaryOperator.ACOS -> OP_ACOS
            UnaryOperator.ATAN -> OP_ATAN
            UnaryOperator.ABS -> OP_ABS
            UnaryOperator.ROUND -> OP_ROUND
            UnaryOperator.FLOOR -> OP_FLOOR
            UnaryOperator.CEIL -> OP_CEIL
            UnaryOperator.LOG -> OP_LOG
            UnaryOperator.SQRT -> OP_SQRT
            UnaryOperator.CBRT -> OP_CBRT
            UnaryOperator.RAND -> OP_RAND
        }
    }
}
