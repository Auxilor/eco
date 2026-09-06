package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import com.willfp.eco.core.leaderboard.TallyProvider
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.Scheduler
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Covers the parts of [LeaderboardService] that do not need a running server: the config
 * reads, the enabled switch, and the start/stop task handling.
 */
class LeaderboardServiceConfigTests {
    private val config = mockk<ConfigYml>()
    private val async = mockk<AsyncTaskContext>()
    private val scheduler = mockk<Scheduler>()
    private val plugin = mockk<EcoPlugin>()

    init {
        every { plugin.configYml } returns config
        every { plugin.id } returns "test"
        every { plugin.scheduler } returns scheduler
        every { scheduler.async() } returns async

        every { config.getBool("leaderboards.enabled") } returns true
        every { config.getInt("leaderboards.refresh-interval") } returns 60
        every { config.getInt("leaderboards.initial-delay") } returns 1
        every { config.getInt("leaderboards.exact-rank-cutoff") } returns 0
        every { config.getInt("leaderboards.max-entries") } returns -1
        every { config.getInt("leaderboards.percent-decimal-places") } returns 1
    }

    private fun service() = LeaderboardService(plugin)

    @Test
    fun `defaults are read from the config`() {
        val service = service()

        assertTrue(service.enabled)
        assertEquals(60L, service.refreshInterval)
        assertEquals(1L, service.initialDelay)
        assertEquals(0, service.exactRankCutoff)
        assertEquals(-1, service.maxEntries)
        assertEquals(1, service.percentDecimalPlaces)
    }

    @Test
    fun `config changes are picked up without restarting the service`() {
        val service = service()

        every { config.getInt("leaderboards.max-entries") } returns 100

        assertEquals(100, service.maxEntries)
    }

    @Test
    fun `a refresh interval below one second is raised to one`() {
        every { config.getInt("leaderboards.refresh-interval") } returns 0

        assertEquals(1L, service().refreshInterval)
    }

    @Test
    fun `a negative initial delay becomes no delay`() {
        every { config.getInt("leaderboards.initial-delay") } returns -5

        assertEquals(0L, service().initialDelay)
    }

    @Test
    fun `starting when disabled schedules nothing`() {
        every { config.getBool("leaderboards.enabled") } returns false

        service().start()

        verify(exactly = 0) { scheduler.async() }
    }

    @Test
    fun `refreshing when disabled never scans the playerbase`() {
        every { config.getBool("leaderboards.enabled") } returns false

        // Eco.get() is null outside a server, so anything that actually reached the data
        // handler would complete this future exceptionally rather than returning null.
        val future = service().refreshAll()

        assertTrue(future.isDone)
        assertFalse(future.isCompletedExceptionally)
        assertNull(future.getNow(null))
    }

    @Test
    fun `refreshing when disabled never scans the playerbase, tallies included`() {
        every { config.getBool("leaderboards.enabled") } returns false

        val service = service()
        val scanned = AtomicInteger()

        service.registerTally(plugin, "jobs") { scanned.incrementAndGet(); emptyMap() }

        val future = service.refreshAll()

        assertTrue(future.isDone)
        assertFalse(future.isCompletedExceptionally)
        assertEquals(0, scanned.get())
    }

    @Test
    fun `one sweep enumerates the playerbase exactly once for leaderboards and tallies`() {
        val eco = mockk<Eco>()
        val enumerations = AtomicInteger()
        val uuids = setOf(UUID.randomUUID(), UUID.randomUUID())

        mockkStatic(Eco::class)

        try {
            every { Eco.get() } returns eco
            every { eco.savedProfileUUIDs } answers {
                enumerations.incrementAndGet()
                uuids
            }

            val service = service()

            val boardCalls = AtomicInteger()
            val tallyCalls = AtomicInteger()

            // Two of each, so a per-item enumeration would show up as four rather than one.
            for (i in 1..2) {
                service.register(plugin, "board$i", LeaderboardValueProvider {
                    boardCalls.incrementAndGet()
                    emptyMap()
                })

                service.registerTally(plugin, "tally$i", TallyProvider {
                    tallyCalls.incrementAndGet()
                    mapOf("bucket" to it.size)
                })
            }

            service.refreshAll().join()

            assertEquals(1, enumerations.get())
            assertEquals(2, boardCalls.get())
            assertEquals(2, tallyCalls.get())

            assertEquals(2, service.getTally("test:tally1")?.getCount("bucket"))
        } finally {
            unmockkStatic(Eco::class)
        }
    }

    @Test
    fun `unregistering a plugin clears its tallies too`() {
        val eco = mockk<Eco>()
        val uuids = setOf(UUID.randomUUID())

        mockkStatic(Eco::class)

        try {
            every { Eco.get() } returns eco
            every { eco.savedProfileUUIDs } returns uuids

            val service = service()
            val tally = service.registerTally(plugin, "jobs") { mapOf("miner" to it.size) }

            service.refreshAll().join()

            assertEquals(1, tally.getCount("miner"))

            service.unregisterAll(plugin)

            assertNull(service.getTally("test:jobs"))
            assertTrue(service.tallies().isEmpty())
            assertEquals(0, tally.getCount("miner"))
        } finally {
            unmockkStatic(Eco::class)
        }
    }

    @Test
    fun `restarting cancels the previous task`() {
        val first = mockk<EcoTask>(relaxed = true)
        val second = mockk<EcoTask>(relaxed = true)

        every {
            async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>())
        } returnsMany listOf(first, second)

        val service = service()

        service.start()
        service.start()

        verify(exactly = 1) { first.cancel() }
        verify(exactly = 0) { second.cancel() }

        service.stop()

        verify(exactly = 1) { second.cancel() }
    }

    @Test
    fun `the refresh runs on the async context in seconds`() {
        every {
            async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>())
        } returns mockk(relaxed = true)

        service().start()

        verify(exactly = 1) {
            async.runTimer(any<Runnable>(), 1L, 60L, TimeUnit.SECONDS)
        }
    }
}
