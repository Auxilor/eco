package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import com.willfp.eco.core.leaderboard.TallyProvider
import com.willfp.eco.core.scheduling.EcoTask
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level

/**
 * The registry of every leaderboard on the server, and the executor that rebuilds them.
 */
class LeaderboardService(
    private val plugin: EcoPlugin
) {
    private val leaderboards = ConcurrentHashMap<String, EcoLeaderboard>()

    // Tallies are kept separately from leaderboards because they answer a different question --
    // how many players are in each bucket, with no ordering -- but they share this service's
    // executor, schedule and playerbase enumeration.
    private val tallies = ConcurrentHashMap<String, EcoPlayerbaseTally>()

    // Ranked keys, mapped to the leaderboards that rank them. Built at registration so that the
    // write hook -- which runs inside the profile writer's per-tick drain -- does a single map
    // lookup rather than scanning every leaderboard on the server on every value change.
    private val byKey = ConcurrentHashMap<PersistentDataKey<*>, MutableSet<EcoLeaderboard>>()

    // Single-threaded on purpose. A refresh is a full-playerbase scan, and running two of them
    // at once would double peak memory and database load for no freshness benefit -- the second
    // scan reads the same data the first one just read. Daemon so a stuck refresh can never hold
    // the JVM open past shutdown.
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "eco-leaderboards").apply { isDaemon = true }
    }

    // The recurring refresh task, or null when the service is stopped or disabled.
    @Volatile
    private var task: EcoTask? = null

    // Guards refreshAll() against overlapping cycles. Set on the caller's thread rather than
    // inside the executor, so that a cycle which overruns its interval is skipped outright
    // rather than queued behind the one still running.
    private val refreshing = AtomicBoolean(false)

    // Every config value is read on access rather than cached, so that /eco reload takes
    // effect without having to tear the service down and build a new one.
    val enabled: Boolean
        get() = plugin.configYml.getBool("leaderboards.enabled")

    val refreshInterval: Long
        get() = plugin.configYml.getInt("leaderboards.refresh-interval").toLong().coerceAtLeast(1)

    val initialDelay: Long
        get() = plugin.configYml.getInt("leaderboards.initial-delay").toLong().coerceAtLeast(0)

    val exactRankCutoff: Int
        get() = plugin.configYml.getInt("leaderboards.exact-rank-cutoff")

    val maxEntries: Int
        get() = plugin.configYml.getInt("leaderboards.max-entries")

    val percentDecimalPlaces: Int
        get() = plugin.configYml.getInt("leaderboards.percent-decimal-places")

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

        // Replacing an existing registration must drop the old leaderboard's index entry too,
        // otherwise a reload leaves it being fed by the write hook forever.
        leaderboards.put(qualified, leaderboard)?.let { unindex(it) }

        leaderboard.keyProvider?.let {
            byKey.computeIfAbsent(it.rankedKey) { ConcurrentHashMap.newKeySet() }.add(leaderboard)
        }

        return leaderboard
    }

    private fun unindex(leaderboard: EcoLeaderboard) {
        val key = leaderboard.keyProvider?.rankedKey ?: return

        byKey[key]?.let {
            it.remove(leaderboard)

            if (it.isEmpty()) {
                byKey.remove(key, it)
            }
        }
    }

    /**
     * Record a value change against every leaderboard that ranks the written key.
     *
     * Called from the profile writer's per-tick drain on the main thread, so this must stay a map
     * lookup and a put: no I/O, no sorting, no scanning. A key that nothing ranks resolves to a
     * null lookup and returns immediately, which is the overwhelmingly common case.
     */
    fun onValueWritten(uuid: UUID, key: PersistentDataKey<*>, value: Any) {
        if (!enabled) {
            return
        }

        val boards = byKey[key] ?: return

        for (board in boards) {
            val provider = board.keyProvider ?: continue
            val values = board.values ?: continue

            // Filtered through the provider so that a single write is subject to exactly the same
            // no-progress rule as a full read. A player dropping back to the key's default leaves
            // the leaderboard rather than being stranded at their last ranked value.
            val ranked = provider.convert(mapOf(uuid to value))[uuid]

            if (ranked == null) {
                values.remove(uuid)
            } else {
                values.put(uuid, ranked)
            }
        }
    }

    /**
     * Register a playerbase tally, replacing any existing tally with the same qualified ID.
     */
    fun registerTally(
        owner: EcoPlugin,
        id: String,
        provider: TallyProvider
    ): EcoPlayerbaseTally {
        val qualified = qualify(owner, id)
        val tally = EcoPlayerbaseTally(qualified, owner, provider, this)

        tallies[qualified] = tally

        return tally
    }

    fun get(id: String): EcoLeaderboard? =
        leaderboards[id]

    fun values(): Collection<EcoLeaderboard> =
        leaderboards.values.toList()

    fun getTally(id: String): EcoPlayerbaseTally? =
        tallies[id]

    fun tallies(): Collection<EcoPlayerbaseTally> =
        tallies.values.toList()

    fun unregisterAll(owner: EcoPlugin) {
        val iterator = leaderboards.entries.iterator()

        while (iterator.hasNext()) {
            val leaderboard = iterator.next().value

            if (leaderboard.plugin.id == owner.id) {
                // Drop the retained snapshot too, otherwise an unregistered leaderboard that
                // something still holds a reference to keeps its entries alive.
                leaderboard.clear()
                unindex(leaderboard)
                iterator.remove()
            }
        }

        val tallyIterator = tallies.entries.iterator()

        while (tallyIterator.hasNext()) {
            val tally = tallyIterator.next().value

            if (tally.plugin.id == owner.id) {
                // Same reasoning as above: drop the retained counts before removing the tally.
                tally.clear()
                tallyIterator.remove()
            }
        }
    }

    /**
     * Start, or restart, the scheduled refresh.
     *
     * Safe to call again at any point: the existing task is always cancelled first, so a
     * reload that changed the interval takes effect immediately.
     */
    fun start() {
        stop()

        // No task at all when disabled, so nothing can reach the data handler.
        if (!enabled) {
            return
        }

        task = plugin.scheduler.async().runTimer(
            Runnable { refreshAll() },
            initialDelay,
            refreshInterval,
            TimeUnit.SECONDS
        )
    }

    /**
     * Cancel the scheduled refresh, if there is one.
     */
    fun stop() {
        task?.cancel()
        task = null
    }

    /**
     * Refresh a single leaderboard off the main thread.
     *
     * Deliberately not covered by the overlap guard: this is one leaderboard, not a full
     * sweep, and a manual refresh should not be silently dropped because a scheduled sweep
     * happens to be running.
     */
    fun refresh(leaderboard: EcoLeaderboard): CompletableFuture<Void> {
        if (!enabled) {
            return CompletableFuture.completedFuture(null)
        }

        return submit {
            rebuild(leaderboard, Eco.get().savedProfileUUIDs)
        } ?: CompletableFuture.completedFuture(null)
    }

    /**
     * Refresh a single tally off the main thread.
     *
     * Deliberately not covered by the overlap guard, for the same reason as
     * [refresh]: a manual refresh of one tally should not be dropped because a scheduled sweep
     * happens to be running.
     */
    fun refresh(tally: EcoPlayerbaseTally): CompletableFuture<Void> {
        if (!enabled) {
            return CompletableFuture.completedFuture(null)
        }

        return submit {
            rebuild(tally, Eco.get().savedProfileUUIDs)
        } ?: CompletableFuture.completedFuture(null)
    }

    /**
     * Refresh every registered leaderboard and tally off the main thread.
     */
    fun refreshAll(): CompletableFuture<Void> {
        if (!enabled) {
            return CompletableFuture.completedFuture(null)
        }

        // A sweep slower than the refresh interval degrades to less-fresh numbers, never to
        // two concurrent full-playerbase scans.
        if (!refreshing.compareAndSet(false, true)) {
            return CompletableFuture.completedFuture(null)
        }

        val future = submit {
            try {
                // Enumerated once and shared. Enumerating per leaderboard would multiply an
                // already-expensive query by the number of skills, jobs and currencies registered
                // on the server.
                val uuids = Eco.get().savedProfileUUIDs

                val (keyed, custom) = leaderboards.values.partition { it.keyProvider != null }

                // Every key-backed leaderboard on the server is read in one batched query per
                // storage type, rather than one query set each. Reading them separately makes as
                // many passes over the same table as there are skills, jobs and currencies.
                if (keyed.isNotEmpty()) {
                    val keys = keyed.mapNotNull { it.keyProvider?.rankedKey }.distinct()
                    val raw = readBatched(keys, uuids)

                    for (leaderboard in keyed) {
                        val provider = leaderboard.keyProvider ?: continue
                        val values = provider.convert(raw[provider.rankedKey].orEmpty())

                        rebuild(leaderboard) { it.rebuildFrom(values, maxEntries) }
                    }
                }

                // A custom provider is opaque, so it reads for itself exactly as it always has.
                for (leaderboard in custom) {
                    rebuild(leaderboard, uuids)
                }

                // Tallies are rebuilt inside the same sweep, from the same uuid set. A tally
                // must never trigger a second enumeration -- sharing this one is the entire
                // reason they live on this service rather than on a schedule of their own.
                for (tally in tallies.values) {
                    rebuild(tally, uuids)
                }
            } catch (e: Exception) {
                // Enumerating the playerbase is the one failure that takes every leaderboard down
                // at once, and the scheduled task discards the future this runs in, so without
                // this catch a failed enumeration would freeze every leaderboard on stale data
                // with nothing logged anywhere.
                plugin.logger.log(Level.WARNING, "Failed to enumerate the playerbase for the leaderboard refresh", e)
            } finally {
                refreshing.set(false)
            }
        }

        if (future == null) {
            // The executor refused the task, so the body -- and its finally -- never runs, and
            // nothing else would ever clear the flag.
            refreshing.set(false)
            return CompletableFuture.completedFuture(null)
        }

        return future
    }

    /**
     * Submit work to the refresh executor, or null if the executor is shutting down.
     */
    private fun submit(action: () -> Unit): CompletableFuture<Void>? =
        try {
            CompletableFuture.runAsync(action, executor)
        } catch (e: RejectedExecutionException) {
            // Shutting down, or already shut down. Nothing to refresh into.
            null
        }

    /**
     * Read every ranked key at once, or an empty map if the read fails.
     *
     * A failed batched read takes every key-backed leaderboard down together, so it is logged here
     * and each leaderboard keeps its previous snapshot, rather than all of them being replaced
     * with empty ones on a transient database problem.
     */
    private fun readBatched(
        keys: List<PersistentDataKey<*>>,
        uuids: Set<UUID>
    ): Map<PersistentDataKey<*>, Map<UUID, Any>> =
        try {
            Eco.get().readAllProfileValuesForKeys(uuids, keys)
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to read leaderboard values", e)
            emptyMap()
        }

    private fun rebuild(leaderboard: EcoLeaderboard, uuids: Set<UUID>) =
        rebuild(leaderboard) { it.rebuild(uuids, maxEntries) }

    private fun rebuild(leaderboard: EcoLeaderboard, action: (EcoLeaderboard) -> Unit) {
        try {
            action(leaderboard)
        } catch (e: Exception) {
            // One plugin shipping a broken value provider must not stop every other leaderboard
            // on the server from refreshing, so the failure is logged and the previous snapshot
            // is kept rather than being replaced with an empty one.
            plugin.logger.log(Level.WARNING, "Failed to refresh leaderboard ${leaderboard.id}", e)
        }
    }

    private fun rebuild(tally: EcoPlayerbaseTally, uuids: Set<UUID>) {
        try {
            tally.rebuild(uuids)
        } catch (e: Exception) {
            // As with leaderboards: one broken provider must not stop everything else on the
            // server from refreshing, so the failure is logged and the previous counts are kept
            // rather than being replaced with empty ones.
            plugin.logger.log(Level.WARNING, "Failed to refresh tally ${tally.id}", e)
        }
    }

    fun shutdown() {
        stop()

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
