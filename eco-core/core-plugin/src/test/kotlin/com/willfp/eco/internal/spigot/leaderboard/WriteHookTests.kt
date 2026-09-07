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
 * The write hook is what lets the full database sweep be demoted from the refresh mechanism to a
 * rare reconcile, so its contract is pinned independently of the scheduling around it.
 */
class WriteHookTests {
    companion object {
        /** Constructing a [PersistentDataKey] registers it through the eco singleton. */
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            mockkStatic(Eco::class)
            every { Eco.get() } returns mockk(relaxed = true)
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
    }

    private val alice: UUID = UUID.randomUUID()

    private var counter = 0

    private fun key(default: Int = 1) = PersistentDataKey(
        namespacedKeyOf("writehook", "level_${counter++}"),
        PersistentDataKeyType.INT,
        default
    )

    private fun serviceRanking(key: PersistentDataKey<*>): LeaderboardService {
        val service = LeaderboardService(plugin)
        service.register(plugin, "board", KeyLeaderboardValueProvider(key))
        return service
    }

    private fun cached(service: LeaderboardService, uuid: UUID) =
        service.get("test:board")?.cachedValue(uuid)

    @Test
    fun `a write to a ranked key updates the cached value`() {
        val key = key()
        val service = serviceRanking(key)

        service.onValueWritten(alice, key, 7)

        assertEquals(7.0, cached(service, alice))
    }

    @Test
    fun `a write at the key default leaves the player unranked`() {
        val key = key(default = 1)
        val service = serviceRanking(key)

        service.onValueWritten(alice, key, 1)

        assertNull(cached(service, alice))
    }

    @Test
    fun `a write that drops a player back to the default removes them from the cache`() {
        val key = key(default = 1)
        val service = serviceRanking(key)

        service.onValueWritten(alice, key, 7)
        service.onValueWritten(alice, key, 1)

        assertNull(cached(service, alice))
    }

    @Test
    fun `a write to a key nothing ranks is ignored`() {
        val key = key()
        val service = serviceRanking(key)

        service.onValueWritten(alice, key(), 7)

        assertNull(cached(service, alice))
    }

    @Test
    fun `a write when leaderboards are disabled is ignored`() {
        val key = key()
        val service = serviceRanking(key)

        every { config.getBool("leaderboards.enabled") } returns false

        service.onValueWritten(alice, key, 7)

        assertNull(cached(service, alice))
    }

    @Test
    fun `a write marks the leaderboard dirty so the next sort publishes it`() {
        val key = key()
        val service = serviceRanking(key)

        service.onValueWritten(alice, key, 7)

        assertEquals(true, service.get("test:board")?.values?.isDirty)
    }

    @Test
    fun `a custom provider leaderboard has no cache to write into`() {
        val service = LeaderboardService(plugin)
        service.register(plugin, "custom") { emptyMap() }

        assertNull(service.get("test:custom")?.values)
    }

    @Test
    fun `unregistering stops a leaderboard from being fed by the hook`() {
        val key = key()
        val service = serviceRanking(key)
        val board = service.get("test:board")

        service.unregisterAll(plugin)
        service.onValueWritten(alice, key, 7)

        assertNull(board?.cachedValue(alice))
    }
}
