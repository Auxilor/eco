package com.willfp.eco.internal.spigot.eventlisteners

import io.mockk.mockk
import org.bukkit.entity.Entity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Collections
import java.util.UUID
import java.util.concurrent.CountDownLatch

internal class LastDamagerTrackerTests {
    private var tick = 0L

    private fun tracker(window: Long = 100) = LastDamagerTracker(window) { tick }

    @Test
    fun `resolves a damager recorded in the same tick`() {
        val tracker = tracker()
        val victim = UUID.randomUUID()
        val damager = mockk<Entity>()

        tracker.record(victim, damager)

        assertEquals(damager, tracker.resolve(victim))
    }

    @Test
    fun `resolves a damager inside the window`() {
        val tracker = tracker(window = 100)
        val victim = UUID.randomUUID()
        val damager = mockk<Entity>()

        tracker.record(victim, damager)
        tick += 99

        assertEquals(damager, tracker.resolve(victim))
    }

    @Test
    fun `does not resolve a damager outside the window`() {
        val tracker = tracker(window = 100)
        val victim = UUID.randomUUID()
        val damager = mockk<Entity>()

        tracker.record(victim, damager)
        tick += 101

        assertNull(tracker.resolve(victim))
    }

    @Test
    fun `a later hit refreshes the window`() {
        val tracker = tracker(window = 100)
        val victim = UUID.randomUUID()
        val first = mockk<Entity>()
        val second = mockk<Entity>()

        tracker.record(victim, first)
        tick += 80
        tracker.record(victim, second)
        tick += 80

        assertEquals(second, tracker.resolve(victim))
    }

    @Test
    fun `resolves null for an unknown victim`() {
        assertNull(tracker().resolve(UUID.randomUUID()))
    }

    @Test
    fun `forget removes the record`() {
        val tracker = tracker()
        val victim = UUID.randomUUID()

        tracker.record(victim, mockk())
        tracker.forget(victim)

        assertNull(tracker.resolve(victim))
    }

    @Test
    fun `purgeExpired drops stale records and keeps fresh ones`() {
        val tracker = tracker(window = 100)
        val stale = UUID.randomUUID()
        val fresh = UUID.randomUUID()
        val damager = mockk<Entity>()

        tracker.record(stale, damager)
        tick += 101
        tracker.record(fresh, damager)

        tracker.purgeExpired()

        assertNull(tracker.resolve(stale))
        assertEquals(damager, tracker.resolve(fresh))
    }

    @Test
    fun `concurrent recording keeps every entry resolvable`() {
        val tracker = tracker(window = Long.MAX_VALUE)
        val victims = (1..8).map { (1..500).map { UUID.randomUUID() } }
        val start = CountDownLatch(1)

        val threads = victims.map { batch ->
            Thread {
                start.await()
                for (victim in batch) {
                    tracker.record(victim, mockk(relaxed = true))
                }
            }
        }

        threads.forEach { it.start() }
        start.countDown()
        threads.forEach { it.join() }

        val missing = victims.flatten().count { tracker.resolve(it) == null }
        assertEquals(0, missing)
    }

    @Test
    fun `purging while recording does not throw`() {
        val tracker = tracker(window = 0L)
        val start = CountDownLatch(1)

        val writer = Thread {
            start.await()
            repeat(5000) { tracker.record(UUID.randomUUID(), mockk(relaxed = true)) }
        }
        val purger = Thread {
            start.await()
            repeat(5000) { tracker.purgeExpired() }
        }

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())
        writer.setUncaughtExceptionHandler { _, e -> failures.add(e) }
        purger.setUncaughtExceptionHandler { _, e -> failures.add(e) }

        writer.start()
        purger.start()
        start.countDown()
        writer.join()
        purger.join()

        assertTrue(failures.isEmpty()) { "threw: ${failures.firstOrNull()}" }
    }
}
