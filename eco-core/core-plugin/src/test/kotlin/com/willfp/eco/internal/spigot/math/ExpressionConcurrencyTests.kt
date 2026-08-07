package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.math.RandomSource
import com.willfp.eco.internal.spigot.math.api.EcoExpressionEnvironmentBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Random
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.ToDoubleFunction

internal class ExpressionConcurrencyTests {

    @Test
    fun `shared compiled expression matches the single-threaded baseline`() {
        val env = EcoExpressionEnvironmentBuilder().variables("x", "y").build()
        val expr = env.compileOrThrow("sqrt(x * x + y * y) + clamp(x, 0, 10) * min(x, y, 3)")

        val inputs = (0 until 64).map { it.toDouble() to (it * 2).toDouble() }
        val baseline = inputs.map { (x, y) -> expr.evaluate(x, y) }

        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).map {
                Callable {
                    repeat(ITERATIONS) {
                        for (i in inputs.indices) {
                            val (x, y) = inputs[i]
                            Assertions.assertEquals(baseline[i], expr.evaluate(x, y), 0.0)
                        }
                    }
                    true
                }
            }
            for (future in pool.invokeAll(tasks)) {
                Assertions.assertTrue(future.get())
            }
        } finally {
            pool.shutdown()
            Assertions.assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `shared environment compiles concurrently`() {
        val env = EcoExpressionEnvironmentBuilder().variables("x").build()
        val expressions = listOf("x + 1", "x * 2", "sqrt(x)", "clamp(x, 0, 5)", "if(x > 2, x, 0)")
        val expected = expressions.map { env.compileOrThrow(it).evaluate(4.0) }

        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).map {
                Callable {
                    repeat(ITERATIONS) {
                        for (i in expressions.indices) {
                            val compiled = env.compileOrThrow(expressions[i])
                            Assertions.assertEquals(expected[i], compiled.evaluate(4.0), 0.0)
                        }
                    }
                    true
                }
            }
            for (future in pool.invokeAll(tasks)) {
                Assertions.assertTrue(future.get())
            }
        } finally {
            pool.shutdown()
            Assertions.assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `independent seeded sources do not interfere`() {
        val expr = EcoExpressionEnvironmentBuilder().build().compileOrThrow("random(0, 1000)")

        // Baseline: what each seed produces alone.
        val expectedA = expr.evaluate(seeded(1L))
        val expectedB = expr.evaluate(seeded(2L))

        val pool = Executors.newFixedThreadPool(2)
        try {
            val a = pool.submit(Callable {
                val source = seeded(1L)
                val first = expr.evaluate(source)
                repeat(ITERATIONS) { expr.evaluate(source) }
                first
            })
            val b = pool.submit(Callable {
                val source = seeded(2L)
                val first = expr.evaluate(source)
                repeat(ITERATIONS) { expr.evaluate(source) }
                first
            })
            Assertions.assertEquals(expectedA, a.get(), 0.0)
            Assertions.assertEquals(expectedB, b.get(), 0.0)
        } finally {
            pool.shutdown()
            Assertions.assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `pooled argument arrays are per-thread`() {
        val identities = ConcurrentHashMap<Long, MutableSet<DoubleArray>>()
        val env = EcoExpressionEnvironmentBuilder().function("record", 1, ToDoubleFunction { args ->
            identities
                .computeIfAbsent(Thread.currentThread().threadId()) {
                    Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap()))
                }
                .add(args)
            args[0]
        }).build()
        val expr = env.compileOrThrow("record(1)")

        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).map {
                Callable { repeat(ITERATIONS) { expr.evaluate() }; true }
            }
            for (future in pool.invokeAll(tasks)) {
                Assertions.assertTrue(future.get())
            }
        } finally {
            pool.shutdown()
            Assertions.assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }

        for ((threadId, arrays) in identities) {
            Assertions.assertEquals(
                1,
                arrays.size,
                "Thread $threadId must reuse exactly one arity-1 array"
            )
        }
    }

    companion object {
        private const val THREADS = 8
        private const val ITERATIONS = 200

        fun seeded(seed: Long): RandomSource {
            val random = Random(seed)
            return RandomSource { random.nextDouble() }
        }
    }
}
