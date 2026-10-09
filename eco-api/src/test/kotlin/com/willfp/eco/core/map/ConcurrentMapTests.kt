package com.willfp.eco.core.map

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

internal class ConcurrentMapTests {
    private fun hammer(threads: Int, action: (Int) -> Unit) {
        val pool = Executors.newFixedThreadPool(threads)
        val start = CountDownLatch(1)

        repeat(threads) { thread ->
            pool.submit {
                start.await()
                action(thread)
            }
        }

        start.countDown()
        pool.shutdown()
        pool.awaitTermination(30, TimeUnit.SECONDS)
    }

    @Test
    fun `get inserts the default once`() {
        val map = DefaultMap<String, MutableList<Int>> { mutableListOf() }
        val first = map["a"]

        assertSame(first, map["a"])
    }

    @Test
    fun `concurrent nested map loses no writes`() {
        val map = concurrentNestedMap<Int, Int, Int>()

        hammer(8) { thread ->
            repeat(1000) { map[it % 10]!![thread * 1000 + it] = it }
        }

        assertEquals(8000, map.values.sumOf { it.size })
    }

    @Test
    fun `concurrent list map loses no appends`() {
        val map = concurrentListMap<Int, Int>()

        hammer(8) { thread ->
            repeat(500) { map.append(it % 5, thread) }
        }

        assertEquals(4000, map.values.sumOf { it.size })
    }

    @Test
    fun `concurrent nested list map loses no appends`() {
        val map = concurrentNestedListMap<Int, Int, Int>()

        hammer(8) { thread ->
            repeat(500) { map[it % 3]!![it % 7].add(thread) }
        }

        assertEquals(4000, map.values.sumOf { inner -> inner.values.sumOf { it.size } })
    }
}
