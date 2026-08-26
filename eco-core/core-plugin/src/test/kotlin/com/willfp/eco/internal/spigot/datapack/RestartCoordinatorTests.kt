package com.willfp.eco.internal.spigot.datapack

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

internal class RestartCoordinatorTests {
    private class Capturing : Handler() {
        val records = mutableListOf<LogRecord>()

        override fun publish(record: LogRecord) {
            records.add(record)
        }

        override fun flush() = Unit

        override fun close() = Unit
    }

    private fun coordinator(): Pair<RestartCoordinator, Capturing> {
        val handler = Capturing()
        val logger = Logger.getAnonymousLogger().apply {
            useParentHandlers = false
            level = Level.ALL
            addHandler(handler)
        }

        return RestartCoordinator(logger) to handler
    }

    @Test
    fun `nothing is pending to start with`() {
        val (coordinator, handler) = coordinator()

        Assertions.assertFalse(coordinator.restartPending)
        coordinator.announce()
        Assertions.assertTrue(handler.records.isEmpty())
    }

    @Test
    fun `three plugins produce one announcement`() {
        val (coordinator, handler) = coordinator()

        coordinator.markPending("a")
        coordinator.markPending("b")
        coordinator.markPending("c")

        coordinator.announce()
        val first = handler.records.size

        coordinator.announce()

        Assertions.assertTrue(first > 0)
        Assertions.assertEquals(first, handler.records.size)
        Assertions.assertTrue(handler.records.any { it.message.contains("a, b, c") })
    }

    @Test
    fun `a new plugin re-arms the announcement`() {
        val (coordinator, handler) = coordinator()

        coordinator.markPending("a")
        coordinator.announce()
        val first = handler.records.size

        coordinator.markPending("b")
        coordinator.announce()

        Assertions.assertTrue(handler.records.size > first)
    }

    @Test
    fun `pending is tracked per plugin`() {
        val (coordinator, _) = coordinator()

        coordinator.markPending("a")

        Assertions.assertTrue(coordinator.isPending("a"))
        Assertions.assertFalse(coordinator.isPending("b"))
        Assertions.assertTrue(coordinator.restartPending)
        Assertions.assertEquals(setOf("a"), coordinator.pendingPlugins())
    }
}
