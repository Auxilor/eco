package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.placeholder.PlaceholderParser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class ExpressionCacheTests {

    private lateinit var parser: PlaceholderParser
    private val context = PlaceholderContext.EMPTY

    @BeforeEach
    fun setup() {
        parser = mockk(relaxed = true)
    }

    /** An evaluator with a long TTL, so caching is observable within a single test. */
    private fun cachingEvaluator() = ExpressionEvaluator(parser, TTL_MS)

    // ---------------------------------------------------------------------
    // Instrumentation
    // ---------------------------------------------------------------------

    @Test
    fun `instrumentation counters start at zero`() {
        val evaluator = cachingEvaluator()
        assertEquals(0L, evaluator.cacheHits)
        assertEquals(0L, evaluator.cacheWrites)
    }

    @Test
    fun `instrumentation counters can be reset`() {
        val evaluator = cachingEvaluator()
        evaluator.evaluate(CACHEABLE, context)
        evaluator.evaluate(CACHEABLE, context)
        assertTrue(evaluator.cacheHits > 0L || evaluator.cacheWrites > 0L)
        evaluator.resetInstrumentation()
        assertEquals(0L, evaluator.cacheHits)
        assertEquals(0L, evaluator.cacheWrites)
    }

    // ---------------------------------------------------------------------
    // Correctness regressions. These fail against the pre-fix implementation.
    // ---------------------------------------------------------------------

    @Test
    fun `placeholder value change within the TTL is not stale`() {
        val evaluator = cachingEvaluator()
        var current = "5"
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { if (it == "%eco_level%") current else "0" }
        }

        assertEquals(10.0, evaluator.evaluate("%eco_level% * 2", context))
        current = "8"
        assertEquals(
            16.0,
            evaluator.evaluate("%eco_level% * 2", context),
            "A placeholder that changed within the TTL must not return a cached value"
        )
    }

    @Test
    fun `rand is not frozen within the TTL`() {
        val evaluator = cachingEvaluator()
        val results = (0 until 50).map { evaluator.evaluate("rand1000000", context) }.toSet()
        assertTrue(
            results.size > 1,
            "rand must produce more than one value within a TTL window, got $results"
        )
    }

    @Test
    fun `random is not frozen within the TTL`() {
        val evaluator = cachingEvaluator()
        val results = (0 until 50).map { evaluator.evaluate("random(0, 1000000)", context) }.toSet()
        assertTrue(
            results.size > 1,
            "random must produce more than one value within a TTL window, got $results"
        )
    }

    @Test
    fun `non-deterministic expression with a placeholder is not frozen`() {
        val evaluator = cachingEvaluator()
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { "1000000" }
        }
        val results = (0 until 50).map { evaluator.evaluate("rand%eco_x%", context) }.toSet()
        assertTrue(results.size > 1, "Got $results")
    }

    // ---------------------------------------------------------------------
    // Branch selection
    // ---------------------------------------------------------------------

    @Test
    fun `non-deterministic expression touches the cache in neither direction`() {
        val evaluator = cachingEvaluator()
        evaluator.evaluate("rand1000000", context)
        evaluator.evaluate("rand1000000", context)
        evaluator.evaluate("random(0, 1000000)", context)
        assertEquals(0L, evaluator.cacheHits, "Non-deterministic expressions must not read the cache")
        assertEquals(0L, evaluator.cacheWrites, "Non-deterministic expressions must not write the cache")
    }

    @Test
    fun `placeholder expression touches the cache in neither direction`() {
        val evaluator = cachingEvaluator()
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { "5" }
        }
        evaluator.evaluate("%eco_level% * 2", context)
        evaluator.evaluate("%eco_level% * 2", context)
        assertEquals(0L, evaluator.cacheHits, "Placeholder expressions must not read the cache")
        assertEquals(0L, evaluator.cacheWrites, "Placeholder expressions must not write the cache")
    }

    @Test
    fun `placeholder expression resolves on every call`() {
        val evaluator = cachingEvaluator()
        var calls = 0
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            calls++
            firstArg<Collection<String>>().map { "5" }
        }
        evaluator.evaluate("%eco_level% * 2", context)
        evaluator.evaluate("%eco_level% * 2", context)
        evaluator.evaluate("%eco_level% * 2", context)
        assertEquals(3, calls, "Placeholder resolution must happen on every call")
    }

    @Test
    fun `expression both placeholder-bearing and non-deterministic takes the bypass`() {
        val evaluator = cachingEvaluator()
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { "1000000" }
        }
        val results = (0 until 50).map { evaluator.evaluate("rand%eco_x%", context) }.toSet()
        assertTrue(results.size > 1, "Got $results")
        assertEquals(0L, evaluator.cacheHits)
        assertEquals(0L, evaluator.cacheWrites)
    }

    // ---------------------------------------------------------------------
    // Preserving the branch that was always correct.
    // ---------------------------------------------------------------------

    @Test
    fun `placeholder-free deterministic expression hits the cache`() {
        val evaluator = cachingEvaluator()
        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
        assertEquals(0L, evaluator.cacheHits, "First call is a miss")
        assertEquals(1L, evaluator.cacheWrites, "First call writes")

        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
        assertEquals(1L, evaluator.cacheHits, "Second call must hit")
        assertEquals(1L, evaluator.cacheWrites, "Second call must not re-write")

        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
        assertEquals(2L, evaluator.cacheHits)
        assertEquals(1L, evaluator.cacheWrites)
    }

    @Test
    fun `cache misses after the TTL expires`() {
        val evaluator = ExpressionEvaluator(parser, 50L)
        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
        assertEquals(1L, evaluator.cacheWrites)

        Thread.sleep(200L)

        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
        assertEquals(0L, evaluator.cacheHits, "Entry must have expired")
        assertEquals(2L, evaluator.cacheWrites, "Expired entry must be rewritten")
    }

    @Test
    fun `different expressions get different keys`() {
        val evaluator = cachingEvaluator()
        assertEquals(4.0, evaluator.evaluate("min(1, 2) + 3", context))
        assertEquals(9.0, evaluator.evaluate("min(4, 5) + 5", context))
        assertEquals(0L, evaluator.cacheHits, "Distinct expressions must not collide")
        assertEquals(2L, evaluator.cacheWrites)
    }

    @Test
    fun `bare numeric strings short-circuit ahead of every branch`() {
        val evaluator = cachingEvaluator()
        assertEquals(42.0, evaluator.evaluate("42", context))
        assertEquals(-1.5, evaluator.evaluate("-1.5", context))
        assertEquals(0L, evaluator.cacheHits)
        assertEquals(0L, evaluator.cacheWrites)
    }

    @Test
    fun `invalid expression still returns null`() {
        val evaluator = cachingEvaluator()
        assertNull(evaluator.evaluate("* 5", context))
        assertNull(evaluator.evaluate("(", context))
    }

    @Test
    fun `non-finite result still returns null and is not cached`() {
        val evaluator = cachingEvaluator()
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { "0" }
        }
        assertNull(evaluator.evaluate("1 / %eco_x%", context))
        assertEquals(0L, evaluator.cacheWrites)
    }

    // ---------------------------------------------------------------------
    // Concurrency. These assert on values only - the counters are non-atomic
    // by design, so they are never asserted on from multiple threads.
    // ---------------------------------------------------------------------

    @Test
    fun `concurrent evaluation across all three branches stays correct`() {
        val evaluator = cachingEvaluator()
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { "7" }
        }

        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).map {
                Callable {
                    repeat(ITERATIONS) {
                        // Branch 3: placeholder-free, deterministic, cached.
                        assertEquals(4.0, evaluator.evaluate(CACHEABLE, context))
                        assertEquals(9.0, evaluator.evaluate("min(4, 5) + 5", context))

                        // Branch 2: placeholder-bearing, uncached.
                        assertEquals(14.0, evaluator.evaluate("%eco_level% * 2", context))

                        // Branch 1: non-deterministic, uncached.
                        val r = evaluator.evaluate("rand1000000", context)
                        assertTrue(r != null && r >= 0.0 && r <= 1000000.0, "Got $r")
                    }
                    true
                }
            }
            for (future in pool.invokeAll(tasks)) {
                assertTrue(future.get())
            }
        } finally {
            pool.shutdown()
            assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `concurrent non-deterministic evaluation still varies`() {
        val evaluator = cachingEvaluator()
        val results = ConcurrentHashMap.newKeySet<Double>()

        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).map {
                Callable {
                    repeat(ITERATIONS) {
                        evaluator.evaluate("rand1000000", context)?.let { results.add(it) }
                    }
                    true
                }
            }
            for (future in pool.invokeAll(tasks)) {
                assertTrue(future.get())
            }
        } finally {
            pool.shutdown()
            assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS))
        }

        assertTrue(results.size > 1, "Concurrent rand must still vary, got ${results.size} values")
    }

    companion object {
        private const val TTL_MS = 60_000L
        private const val THREADS = 8
        private const val ITERATIONS = 200

        /**
         * Placeholder-free, deterministic, and NOT constant-folded: `min` is a FunctionCall, and
         * ShuntingYard folds only binary operations over literals and constants.
         */
        private const val CACHEABLE = "min(1, 2) + 3"
    }
}
