package com.willfp.eco.core.registry

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class RegistryConcurrencyTests {
    private class Element(private val id: String) : Registrable {
        override fun getID() = id
    }

    @Test
    fun `concurrent registers are all kept`() {
        val registry = Registry<Element>()
        val pool = Executors.newFixedThreadPool(8)
        val start = CountDownLatch(1)

        repeat(8) { thread ->
            pool.submit {
                start.await()
                repeat(500) { registry.register(Element("e_${thread}_$it")) }
            }
        }

        start.countDown()
        pool.shutdown()
        pool.awaitTermination(30, TimeUnit.SECONDS)

        assertEquals(4000, registry.values().size)
    }

    @Test
    fun `values is never stale after a mutation`() {
        val registry = Registry<Element>()
        val pool = Executors.newFixedThreadPool(4)
        val start = CountDownLatch(1)

        repeat(3) {
            pool.submit {
                start.await()
                repeat(5000) { registry.values() }
            }
        }
        pool.submit {
            start.await()
            repeat(1000) { registry.register(Element("e_$it")) }
        }

        start.countDown()
        pool.shutdown()
        pool.awaitTermination(30, TimeUnit.SECONDS)

        assertEquals(1000, registry.values().size)
    }

    @Test
    fun `locked registry rejects register and remove`() {
        val registry = Registry<Element>()
        val element = registry.register(Element("a"))
        val locker = Any()
        registry.lock(locker)

        assertThrows(IllegalStateException::class.java) { registry.register(Element("b")) }
        assertThrows(IllegalStateException::class.java) { registry.remove(element) }
        assertThrows(IllegalArgumentException::class.java) { registry.unlock(Any()) }

        registry.unlock(locker)
        registry.remove(element)
        assertEquals(0, registry.values().size)
    }
}
