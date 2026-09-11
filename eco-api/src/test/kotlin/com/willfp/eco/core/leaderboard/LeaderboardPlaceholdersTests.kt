package com.willfp.eco.core.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.util.UUID
import java.util.concurrent.CompletableFuture
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private class StubLeaderboard(
    private val plugin: EcoPlugin,
    private val snapshot: LeaderboardSnapshot,
    private val rank: LeaderboardRank
) : Leaderboard {
    override fun getId() = "test:mining"

    override fun getPlugin() = plugin

    override fun getSnapshot() = snapshot

    override fun getRank(uuid: UUID) = rank

    override fun refresh(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)
}

private class StubTally(
    private val plugin: EcoPlugin,
    private val counts: TallyCounts
) : PlayerbaseTally {
    override fun getId() = "test:jobs"

    override fun getPlugin() = plugin

    override fun getCounts() = counts

    override fun refresh(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)
}

/**
 * Covers the parts of the standard placeholder set that do not need a running server.
 *
 * The registry itself ([PlaceholderManager.registerPlaceholder]) is a plain static map, so
 * registration and resolution are both reachable here. What is not reachable is the resolved
 * value of `%..._top_<N>_name%` for a position that exists: [LeaderboardEntry.getPlayer] calls
 * through to Bukkit's OfflinePlayer cache, which needs a server. Only its empty paths are
 * tested.
 */
internal class LeaderboardPlaceholdersTests {
    companion object {
        /**
         * [com.willfp.eco.util.PatternUtils], which literal placeholder identifiers compile
         * through, reads eco's config in its static initializer. That runs once per JVM and
         * poisons the class permanently if it throws, so Eco has to be stubbed before the first
         * registration rather than per test.
         */
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            val configYml = mockk<ConfigYml>()
            every { configYml.getInt("literal-cache-ttl") } returns 10

            val ecoPlugin = mockk<EcoPlugin>()
            every { ecoPlugin.configYml } returns configYml

            val eco = mockk<Eco>()
            every { eco.ecoPlugin } returns ecoPlugin

            mockkStatic(Eco::class)
            every { Eco.get() } returns eco
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val plugin = mockk<EcoPlugin>()

    private val ranked = UUID.randomUUID()
    private val unranked = UUID.randomUUID()

    private val snapshot = LeaderboardSnapshot(
        listOf(
            LeaderboardEntry(1, ranked, 1234.5),
            LeaderboardEntry(2, UUID.randomUUID(), 100.0)
        ),
        mapOf(ranked to 1),
        50,
        0L
    )

    private fun player(uuid: UUID): Player {
        val player = mockk<Player>()
        every { player.uniqueId } returns uuid
        return player
    }

    private fun register(
        rank: LeaderboardRank = LeaderboardRank.exact(1),
        emptyText: String = "N/A"
    ): Leaderboard {
        val leaderboard = StubLeaderboard(plugin, snapshot, rank)
        leaderboard.registerStandardPlaceholders(plugin, "mining", emptyText) { "$it coins" }
        return leaderboard
    }

    private fun resolve(args: String, uuid: UUID? = null): String? {
        val placeholder = PlaceholderManager.getRegisteredPlaceholder(plugin, args) ?: return null
        val context = if (uuid == null) PlaceholderContext() else PlaceholderContext(player(uuid))
        return placeholder.getValue(args, context)
    }

    @Test
    fun `rank is the exact position`() {
        register()
        assertEquals("1", resolve("mining_rank", ranked))
    }

    @Test
    fun `rank is the empty text when unranked`() {
        register()
        assertEquals("N/A", resolve("mining_rank", unranked))
    }

    @Test
    fun `rank is never a percentile`() {
        register(rank = LeaderboardRank.percent(12.5))

        assertEquals("1", resolve("mining_rank", ranked), "reads getPosition, not getRank")
        assertEquals("Top 12.5%", resolve("mining_rank_display", ranked))
    }

    @Test
    fun `rank display renders an exact rank`() {
        register(rank = LeaderboardRank.exact(3))
        assertEquals("#3", resolve("mining_rank_display", ranked))
    }

    @Test
    fun `rank display is the empty text when unranked`() {
        register(rank = LeaderboardRank.unranked())
        assertEquals("N/A", resolve("mining_rank_display", ranked))
    }

    @Test
    fun `tracked is the tracked player count of the snapshot`() {
        register()
        assertEquals("50", resolve("mining_tracked"))
    }

    @Test
    fun `top value is formatted by the caller's formatter`() {
        register()
        assertEquals("1234.5 coins", resolve("mining_top_1_value"))
        assertEquals("100.0 coins", resolve("mining_top_2_value"))
    }

    @Test
    fun `top value beyond the retained entries is the empty text`() {
        register()
        assertEquals("N/A", resolve("mining_top_3_value"))
        assertEquals("N/A", resolve("mining_top_0_value"))
    }

    @Test
    fun `top name beyond the retained entries is the empty text`() {
        register()
        assertEquals("N/A", resolve("mining_top_3_name"))
    }

    @Test
    fun `a position too large to parse is the empty text`() {
        register()
        assertEquals("N/A", resolve("mining_top_99999999999999_value"))
        assertEquals("N/A", resolve("mining_top_99999999999999_name"))
    }

    @Test
    fun `a non-numeric position matches no placeholder`() {
        register()
        assertNull(resolve("mining_top_x_value"))
    }

    @Test
    fun `the empty text is configurable`() {
        register(rank = LeaderboardRank.unranked(), emptyText = "-")
        assertEquals("-", resolve("mining_rank_display", ranked))
        assertEquals("-", resolve("mining_top_9_value"))
    }

    @Test
    fun `re-registering the same prefix replaces rather than appends`() {
        register()
        val before = PlaceholderManager.getRegisteredPlaceholders(plugin).size

        register()

        assertEquals(5, before, "rank, rank_display, tracked, top name, top value")
        assertEquals(before, PlaceholderManager.getRegisteredPlaceholders(plugin).size)
    }

    @Test
    fun `tally buckets are exposed as counts`() {
        val tally = StubTally(plugin, TallyCounts.of(mapOf("miner" to 7, "farmer" to 0)))
        tally.registerStandardPlaceholders(plugin, "jobs", "N/A")

        assertEquals("7", resolve("jobs_miner_count"))
        assertEquals("0", resolve("jobs_farmer_count"), "a bucket that exists but is empty is zero, not the empty text")
        assertEquals("N/A", resolve("jobs_lumberjack_count"), "a bucket the refresh did not produce is the empty text")
    }

    @Test
    fun `an exact rank renders as a hash`() {
        assertEquals("#4182", LeaderboardRank.exact(4182).toDisplayString("N/A"))
    }

    @Test
    fun `a whole percentile renders without a decimal part`() {
        assertEquals("Top 5%", LeaderboardRank.percent(5.0).toDisplayString("N/A"))
        assertEquals("Top 12.5%", LeaderboardRank.percent(12.5).toDisplayString("N/A"))
    }

    @Test
    fun `an unranked standing renders as the empty text`() {
        assertEquals("N/A", LeaderboardRank.unranked().toDisplayString("N/A"))
    }
}
