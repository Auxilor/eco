package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The registry of every leaderboard on the server, and the executor that rebuilds them.
 */
class LeaderboardService(
    private val plugin: EcoPlugin
) {
    private val leaderboards = ConcurrentHashMap<String, EcoLeaderboard>()

    // Single-threaded on purpose. A refresh is a full-playerbase scan, and running two of them
    // at once would double peak memory and database load for no freshness benefit -- the second
    // scan reads the same data the first one just read. Daemon so a stuck refresh can never hold
    // the JVM open past shutdown.
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "eco-leaderboards").apply { isDaemon = true }
    }

    // Task 6 replaces these with config reads.
    val exactRankCutoff: Int
        get() = 0

    val maxEntries: Int
        get() = -1

    val percentDecimalPlaces: Int
        get() = 1

    /**
     * Register a leaderboard, replacing any existing leaderboard with the same qualified ID.
     */
    fun register(
        owner: EcoPlugin,
        id: String,
        provider: LeaderboardValueProvider
    ): EcoLeaderboard {
        val qualified = qualify(owner, id)
        val leaderboard = EcoLeaderboard(qualified, owner, provider, this)

        leaderboards[qualified] = leaderboard

        return leaderboard
    }

    fun get(id: String): EcoLeaderboard? =
        leaderboards[id]

    fun values(): Collection<EcoLeaderboard> =
        leaderboards.values.toList()

    fun unregisterAll(owner: EcoPlugin) {
        val iterator = leaderboards.entries.iterator()

        while (iterator.hasNext()) {
            val leaderboard = iterator.next().value

            if (leaderboard.plugin.id == owner.id) {
                // Drop the retained snapshot too, otherwise an unregistered leaderboard that
                // something still holds a reference to keeps its entries alive.
                leaderboard.clear()
                iterator.remove()
            }
        }
    }

    /**
     * Refresh a single leaderboard off the main thread.
     */
    fun refresh(leaderboard: EcoLeaderboard): CompletableFuture<Void> =
        CompletableFuture.runAsync({
            rebuild(leaderboard, Eco.get().savedProfileUUIDs)
        }, executor)

    /**
     * Refresh every registered leaderboard off the main thread.
     */
    fun refreshAll(): CompletableFuture<Void> =
        CompletableFuture.runAsync({
            // Enumerated once and shared. Enumerating per leaderboard would multiply an
            // already-expensive query by the number of skills, jobs and currencies registered
            // on the server.
            val uuids = Eco.get().savedProfileUUIDs

            for (leaderboard in leaderboards.values) {
                rebuild(leaderboard, uuids)
            }
        }, executor)

    private fun rebuild(leaderboard: EcoLeaderboard, uuids: Set<UUID>) {
        try {
            leaderboard.rebuild(uuids, maxEntries)
        } catch (e: Exception) {
            // One plugin shipping a broken value provider must not stop every other leaderboard
            // on the server from refreshing, so the failure is logged and the previous snapshot
            // is kept rather than being replaced with an empty one.
            plugin.logger.warning("Failed to refresh leaderboard ${leaderboard.id}: $e")
            e.printStackTrace()
        }
    }

    fun shutdown() {
        executor.shutdown()

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    private fun qualify(owner: EcoPlugin, id: String) =
        "${owner.id}:$id"
}
