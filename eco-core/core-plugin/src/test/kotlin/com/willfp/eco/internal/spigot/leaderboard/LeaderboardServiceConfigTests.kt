package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import com.willfp.eco.core.leaderboard.TallyProvider
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.Scheduler
import com.willfp.eco.util.namespacedKeyOf
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
        every { config.getInt("leaderboards.sort-interval") } returns 5
        every { config.getInt("leaderboards.max-reconcile-overlay") } returns 10000
        every { config.has("leaderboards.reconcile-interval") } returns true
        every { config.getInt("leaderboards.reconcile-interval") } returns 3600
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
    fun `restarting cancels the previous tasks`() {
        // Two timers per start now -- the sort tick and the reconcile sweep -- so a restart has
        // two handles to cancel, not one.
        val firstSort = mockk<EcoTask>(relaxed = true)
        val firstReconcile = mockk<EcoTask>(relaxed = true)
        val secondSort = mockk<EcoTask>(relaxed = true)
        val secondReconcile = mockk<EcoTask>(relaxed = true)

        every {
            async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>())
        } returnsMany listOf(firstSort, firstReconcile, secondSort, secondReconcile)

        every { async.runLater(any<Runnable>(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)

        val service = service()

        service.start()
        service.start()

        verify(exactly = 1) { firstSort.cancel() }
        verify(exactly = 1) { firstReconcile.cancel() }
        verify(exactly = 0) { secondSort.cancel() }
        verify(exactly = 0) { secondReconcile.cancel() }

        service.stop()

        verify(exactly = 1) { secondSort.cancel() }
        verify(exactly = 1) { secondReconcile.cancel() }
    }

    @Test
    fun `the startup load runs once on the async context in seconds`() {
        every {
            async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>())
        } returns mockk(relaxed = true)

        every { async.runLater(any<Runnable>(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)

        service().start()

        // One-shot at the initial delay: this is what populates the caches on boot, and on a
        // server with reconciliation off it is the only database read there ever is.
        verify(exactly = 1) {
            async.runLater(any<Runnable>(), 1L, TimeUnit.SECONDS)
        }
    }

    @Test
    fun `a sweep reads every key-backed leaderboard in one batched call`() {
        val eco = mockk<Eco>(relaxed = true)
        val uuids = setOf(UUID.randomUUID(), UUID.randomUUID())

        mockkStatic(Eco::class)

        try {
            every { Eco.get() } returns eco
            every { eco.savedProfileUUIDs } returns uuids

            val batchedCalls = AtomicInteger()
            val perKeyCalls = AtomicInteger()
            val batchedKeys = HashSet<PersistentDataKey<*>>()

            every { eco.readAllProfileValuesForKeys(any(), any()) } answers {
                batchedCalls.incrementAndGet()
                batchedKeys.addAll(secondArg<Collection<PersistentDataKey<*>>>())
                emptyMap()
            }

            every { eco.readAllProfileValues(any(), any<PersistentDataKey<Any>>()) } answers {
                perKeyCalls.incrementAndGet()
                emptyMap()
            }

            val service = service()

            // Three keys, so a per-key read would show up as three calls rather than one.
            val keys = (1..3).map {
                PersistentDataKey(
                    namespacedKeyOf("sweeptest", "key_$it"),
                    PersistentDataKeyType.INT,
                    0
                )
            }

            for ((index, key) in keys.withIndex()) {
                service.register(plugin, "board_$index", KeyLeaderboardValueProvider(key))
            }

            service.refreshAll().join()

            assertEquals(1, batchedCalls.get())
            assertEquals(0, perKeyCalls.get())
            assertEquals(keys.toSet(), batchedKeys)
        } finally {
            unmockkStatic(Eco::class)
        }
    }

    @Test
    fun `a custom provider leaderboard is still refreshed alongside batched ones`() {
        val eco = mockk<Eco>(relaxed = true)
        val uuids = setOf(UUID.randomUUID())

        mockkStatic(Eco::class)

        try {
            every { Eco.get() } returns eco
            every { eco.savedProfileUUIDs } returns uuids
            every { eco.readAllProfileValuesForKeys(any(), any()) } returns emptyMap()

            val service = service()
            val customCalls = AtomicInteger()

            // A custom provider is opaque, so it cannot be batched -- but it must not be dropped
            // from the sweep just because the key-backed ones now take a different path.
            service.register(plugin, "custom", LeaderboardValueProvider {
                customCalls.incrementAndGet()
                emptyMap()
            })

            service.register(
                plugin,
                "keyed",
                KeyLeaderboardValueProvider(
                    PersistentDataKey(
                        namespacedKeyOf("sweeptest", "keyed"),
                        PersistentDataKeyType.INT,
                        0
                    )
                )
            )

            service.refreshAll().join()

            assertEquals(1, customCalls.get())
        } finally {
            unmockkStatic(Eco::class)
        }
    }

    @Test
    fun `the reconcile interval falls back to the deprecated refresh interval`() {
        every { config.has("leaderboards.reconcile-interval") } returns false
        every { config.getInt("leaderboards.refresh-interval") } returns 120

        assertEquals(120L, service().reconcileInterval)
    }

    @Test
    fun `a sort interval below one second is raised to one`() {
        every { config.getInt("leaderboards.sort-interval") } returns 0

        assertEquals(1L, service().sortInterval)
    }

    @Test
    fun `a negative reconcile interval becomes zero rather than going backwards`() {
        every { config.getInt("leaderboards.reconcile-interval") } returns -5

        assertEquals(0L, service().reconcileInterval)
    }

    @Test
    fun `a zero reconcile interval still loads once at startup but schedules no sweep`() {
        every { config.getInt("leaderboards.reconcile-interval") } returns 0
        every { async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)
        every { async.runLater(any<Runnable>(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)

        service().start()

        // The sort tick, and nothing else recurring.
        verify(exactly = 1) { async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>()) }

        // The startup load still happens, or the caches would never be populated at all.
        verify(exactly = 1) { async.runLater(any<Runnable>(), any(), any<TimeUnit>()) }
    }

    @Test
    fun `a positive reconcile interval schedules the sweep offset past the startup load`() {
        every { async.runTimer(any<Runnable>(), any(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)
        every { async.runLater(any<Runnable>(), any(), any<TimeUnit>()) } returns mockk(relaxed = true)

        service().start()

        // Sort tick at the initial delay, reconcile one full interval after it, so the startup
        // load is not immediately repeated.
        verify(exactly = 1) { async.runTimer(any<Runnable>(), 1L, 5L, TimeUnit.SECONDS) }
        verify(exactly = 1) { async.runTimer(any<Runnable>(), 3601L, 3600L, TimeUnit.SECONDS) }
    }

    @Test
    fun `only dirty leaderboards are sorted`() {
        mockkStatic(Eco::class)
        every { Eco.get() } returns mockk(relaxed = true)

        val key = try {
            PersistentDataKey(
                namespacedKeyOf("sorttest", "level"),
                PersistentDataKeyType.INT,
                0
            )
        } finally {
            unmockkStatic(Eco::class)
        }

        val service = service()
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        // Nothing written, so there is nothing to publish.
        service.sortDirty()
        assertEquals(0, board.snapshot.trackedPlayers)

        service.onValueWritten(UUID.randomUUID(), key, 5)
        service.sortDirty()

        assertEquals(1, board.snapshot.trackedPlayers)
    }

    @Test
    fun `sorting clears the dirty flag so an unchanged leaderboard is not re-sorted`() {
        mockkStatic(Eco::class)
        every { Eco.get() } returns mockk(relaxed = true)

        val key = try {
            PersistentDataKey(
                namespacedKeyOf("sorttest", "level_clean"),
                PersistentDataKeyType.INT,
                0
            )
        } finally {
            unmockkStatic(Eco::class)
        }

        val service = service()
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        service.onValueWritten(UUID.randomUUID(), key, 5)
        service.sortDirty()

        assertFalse(board.values!!.isDirty)
    }
}
