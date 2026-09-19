package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.Scheduler
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Covers the parts of [EcoLeaderboard] that do not need a running server.
 */
class EcoLeaderboardTests {
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
        // Anything past first place is reported as a percentile, so getRank has to divide.
        every { config.getInt("leaderboards.exact-rank-cutoff") } returns 1
        every { config.getInt("leaderboards.max-entries") } returns -1
        every { config.getInt("leaderboards.percent-decimal-places") } returns 1
    }

    private val target = UUID.randomUUID()

    // The target always sorts last, so its rank is exactly the size of the uuid set it was
    // built from, and its percentile is always exactly 100%.
    private val provider = LeaderboardValueProvider { uuids ->
        uuids.associateWith { if (it == target) 0.0 else 1.0 }
    }

    private fun leaderboard(): EcoLeaderboard =
        EcoLeaderboard("test:leaderboard", plugin, provider, LeaderboardService(plugin))

    private fun playerbase(size: Int): Set<UUID> =
        (setOf(target) + List(size - 1) { UUID.randomUUID() }).toSet()

    @Test
    fun `the last position of any playerbase is the hundredth percentile`() {
        val leaderboard = leaderboard()

        leaderboard.rebuild(playerbase(100), -1)

        assertEquals(100, leaderboard.getPosition(target))
        assertEquals(100.0, leaderboard.getRank(target).percent)
    }

    @Test
    fun `getRank never mixes a rank and a player count from different snapshots`() {
        val leaderboard = leaderboard()

        val large = playerbase(20)
        val small = playerbase(2)

        leaderboard.rebuild(large, -1)

        val running = AtomicBoolean(true)

        // Alternates the published snapshot between a hundred-player generation, where the
        // target ranks 100th of 100, and a two-player generation, where it ranks 2nd of 2. Both
        // generations put the target in exactly the hundredth percentile, so any percentile
        // above that can only have come from reading the rank of one generation against the
        // tracked-player count of another.
        val swapper = Thread {
            while (running.get()) {
                leaderboard.rebuild(small, -1)
                leaderboard.rebuild(large, -1)
            }
        }

        swapper.start()

        try {
            repeat(50_000) {
                val percent = leaderboard.getRank(target).percent

                assertTrue(
                    percent == null || percent <= 100.0,
                    "getRank tore across two snapshots and reported $percent%"
                )

                // getRank reads the cutoff and the decimal places off the mocked config, and
                // MockK retains every one of those calls for verification. Nothing here verifies
                // them, so they are dropped as the loop runs rather than being allowed to fill
                // the heap.
                if ((it + 1) % 1_000 == 0) {
                    clearMocks(config, answers = false, recordedCalls = true)
                }
            }
        } finally {
            running.set(false)
            swapper.join()
        }
    }
}
