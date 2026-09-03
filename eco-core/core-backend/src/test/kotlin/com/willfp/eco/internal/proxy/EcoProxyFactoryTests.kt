package com.willfp.eco.internal.proxy

import com.willfp.eco.core.EcoPlugin
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CountDownLatch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class EcoProxyFactoryTests {
    interface FirstProxy
    interface SecondProxy

    private fun plugin(): EcoPlugin = mockk(relaxed = true) {
        every { proxyPackage } returns "com.willfp.eco.internal.proxy.fixture"
    }

    @Test
    fun `concurrent lookups do not corrupt the cache`() {
        val factory = EcoProxyFactory(plugin())
        val start = CountDownLatch(1)
        val failures = mutableListOf<Throwable>()

        val threads = (1..8).map {
            Thread {
                start.await()
                repeat(2000) {
                    runCatching { factory.getProxy(FirstProxy::class.java) }
                    runCatching { factory.getProxy(SecondProxy::class.java) }
                }
            }.apply {
                setUncaughtExceptionHandler { _, e -> synchronized(failures) { failures.add(e) } }
            }
        }

        threads.forEach { it.start() }
        start.countDown()
        threads.forEach { it.join(30_000) }

        Assertions.assertTrue(threads.none { it.isAlive }) { "a lookup thread hung" }
        Assertions.assertTrue(failures.isEmpty()) { "threw: ${failures.firstOrNull()}" }
    }
}
