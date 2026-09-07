package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.Scheduler
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * A write landing between a reconcile's database read and its install is newer than what the read
 * returned. Replacing wholesale would silently revert it, which surfaces as one player's value
 * being wrong roughly once a week -- so the sweep is driven end to end here rather than trusting
 * the unit-level cover on [PlayerbaseValues].
 */
class ReconcileRaceTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            mockkStatic(Eco::class)
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val config = mockk<ConfigYml>()
    private val scheduler = mockk<Scheduler>()
    private val async = mockk<AsyncTaskContext>()
    private val plugin = mockk<EcoPlugin>()

    init {
        every { plugin.configYml } returns config
        every { plugin.id } returns "test"
        every { plugin.scheduler } returns scheduler
        every { scheduler.async() } returns async

        every { config.getBool("leaderboards.enabled") } returns true
        every { config.getInt("leaderboards.max-entries") } returns -1
        every { config.getInt("leaderboards.exact-rank-cutoff") } returns 0
        every { config.getInt("leaderboards.percent-decimal-places") } returns 1
        every { config.getInt("leaderboards.max-reconcile-overlay") } returns 10000
    }

    private val alice: UUID = UUID.randomUUID()
    private val bob: UUID = UUID.randomUUID()

    private var counter = 0

    @Test
    fun `a write landing during the sweep survives it`() {
        val eco = mockk<Eco>(relaxed = true)
        every { Eco.get() } returns eco

        val key = PersistentDataKey(
            namespacedKeyOf("racetest", "level_${counter++}"),
            PersistentDataKeyType.INT,
            0
        )

        val service = LeaderboardService(plugin)
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        every { eco.savedProfileUUIDs } returns setOf(alice, bob)

        // The write lands while the read is in flight: the read has already been started, and
        // returns the value from *before* the player levelled up.
        every { eco.readAllProfileValuesForKeys(any(), any()) } answers {
            service.onValueWritten(alice, key, 42)
            mapOf<PersistentDataKey<*>, Map<UUID, Any>>(key to mapOf(alice to 5, bob to 9))
        }

        service.refreshAll().join()

        assertEquals(42.0, board.cachedValue(alice), "the newer write must win over the stale read")
        assertEquals(9.0, board.cachedValue(bob), "untouched players come from the read")

        // And the published snapshot reflects it, rather than only the cache.
        assertEquals(1, board.getPosition(alice), "42 outranks 9")
        assertEquals(2, board.getPosition(bob))
    }

    @Test
    fun `a sweep with no concurrent writes installs the database values`() {
        val eco = mockk<Eco>(relaxed = true)
        every { Eco.get() } returns eco

        val key = PersistentDataKey(
            namespacedKeyOf("racetest", "level_${counter++}"),
            PersistentDataKeyType.INT,
            0
        )

        val service = LeaderboardService(plugin)
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        every { eco.savedProfileUUIDs } returns setOf(alice)
        every { eco.readAllProfileValuesForKeys(any(), any()) } returns
                mapOf<PersistentDataKey<*>, Map<UUID, Any>>(key to mapOf(alice to 5))

        service.refreshAll().join()

        assertEquals(5.0, board.cachedValue(alice))
        assertEquals(1, board.snapshot.trackedPlayers)
    }

    @Test
    fun `a sweep publishes without waiting for the next sort tick`() {
        val eco = mockk<Eco>(relaxed = true)
        every { Eco.get() } returns eco

        val key = PersistentDataKey(
            namespacedKeyOf("racetest", "level_${counter++}"),
            PersistentDataKeyType.INT,
            0
        )

        val service = LeaderboardService(plugin)
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        every { eco.savedProfileUUIDs } returns setOf(alice)
        every { eco.readAllProfileValuesForKeys(any(), any()) } returns
                mapOf<PersistentDataKey<*>, Map<UUID, Any>>(key to mapOf(alice to 5))

        service.refreshAll().join()

        // The startup load has to take effect immediately, or leaderboards would rank nobody
        // until the first sort tick fired after it.
        assertEquals(1, board.getPosition(alice))
    }

    @Test
    fun `players at the key default are dropped by the sweep`() {
        val eco = mockk<Eco>(relaxed = true)
        every { Eco.get() } returns eco

        val key = PersistentDataKey(
            namespacedKeyOf("racetest", "level_${counter++}"),
            PersistentDataKeyType.INT,
            1
        )

        val service = LeaderboardService(plugin)
        val board = service.register(plugin, "board", KeyLeaderboardValueProvider(key))

        every { eco.savedProfileUUIDs } returns setOf(alice, bob)
        every { eco.readAllProfileValuesForKeys(any(), any()) } returns
                mapOf<PersistentDataKey<*>, Map<UUID, Any>>(key to mapOf(alice to 1, bob to 7))

        service.refreshAll().join()

        assertNull(board.cachedValue(alice), "a player on the start level has made no progress")
        assertEquals(1, board.snapshot.trackedPlayers, "and is out of the percentile denominator")
    }
}
