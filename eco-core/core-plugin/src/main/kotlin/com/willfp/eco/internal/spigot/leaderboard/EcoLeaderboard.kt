package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.leaderboard.Leaderboard
import com.willfp.eco.core.leaderboard.LeaderboardEntry
import com.willfp.eco.core.leaderboard.LeaderboardRank
import com.willfp.eco.core.leaderboard.LeaderboardSnapshot
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import java.util.UUID
import java.util.concurrent.CompletableFuture

class EcoLeaderboard(
    private val id: String,
    private val owner: EcoPlugin,
    private val provider: LeaderboardValueProvider,
    private val service: LeaderboardService
) : Leaderboard {
    // Written on the refresh executor, read from the main thread (placeholders, GUI lore), so
    // publication of the new snapshot has to be guaranteed. The snapshot itself is immutable,
    // so a volatile reference swap is all the synchronisation a reader needs.
    @Volatile
    private var snapshot: LeaderboardSnapshot = LeaderboardSnapshot.EMPTY

    override fun getId() = id

    override fun getPlugin() = owner

    override fun getSnapshot() = snapshot

    override fun getRank(uuid: UUID): LeaderboardRank {
        // Read the volatile field once: a refresh landing between two reads would otherwise mix a
        // rank from one snapshot generation with a tracked-player count from the next, producing
        // a percentile that can exceed 100% or be wrongly flattering.
        val snapshot = this.snapshot

        return LeaderboardRank.of(
            snapshot.getRank(uuid),
            snapshot.trackedPlayers,
            service.exactRankCutoff,
            service.percentDecimalPlaces
        )
    }

    override fun refresh(): CompletableFuture<Void> = service.refresh(this)

    /**
     * The key-backed provider, or null if this leaderboard ranks by a custom provider.
     *
     * A key-backed leaderboard can have its values read in one batched query alongside every other
     * key-backed leaderboard on the server, instead of issuing a read of its own. A custom
     * provider is opaque, so there is nothing to batch and it keeps reading for itself.
     */
    internal val keyProvider: KeyLeaderboardValueProvider?
        get() = provider as? KeyLeaderboardValueProvider

    /**
     * The cached values this leaderboard sorts, or null if it ranks by a custom provider.
     *
     * A custom provider is opaque, so there is nothing to update incrementally and nothing worth
     * caching: those leaderboards are rebuilt by the reconcile sweep only, exactly as every
     * leaderboard used to be.
     */
    internal val values: PlayerbaseValues? =
        if (provider is KeyLeaderboardValueProvider) PlayerbaseValues() else null

    /** The cached value for a player, or null if they are unranked. */
    internal fun cachedValue(uuid: UUID): Double? = values?.snapshot()?.get(uuid)

    /**
     * Re-sort from the cached values and publish.
     *
     * Called only from the refresh executor.
     */
    internal fun sortCached(maxEntries: Int) {
        val values = this.values ?: return

        rebuildFrom(values.snapshot(), maxEntries)

        // Cleared after the sort rather than before it, so a write landing mid-sort leaves the
        // leaderboard dirty and is picked up by the next tick instead of being lost.
        values.clearDirty()
    }

    /** Build a new snapshot. Called only from the refresh executor. */
    internal fun rebuild(uuids: Set<UUID>, maxEntries: Int) =
        rebuildFrom(provider.readValues(uuids), maxEntries)

    /**
     * Build a new snapshot from values that have already been read.
     *
     * Called only from the refresh executor.
     */
    internal fun rebuildFrom(values: Map<UUID, Double>, maxEntries: Int) {
        // The UUID tiebreak is deliberate: without it, two players on equal values are ordered
        // by whatever the sort happened to do that time, and visibly swap places on every
        // refresh.
        val sorted = values.entries
            .sortedWith(compareByDescending<Map.Entry<UUID, Double>> { it.value }.thenBy { it.key })
            .map { it.key to it.value }

        // Ranks are never capped, so a player outside the retained top-N can still find their
        // own position.
        val ranks = HashMap<UUID, Int>(sorted.size)
        for ((index, pair) in sorted.withIndex()) {
            ranks[pair.first] = index + 1
        }

        // A negative max means uncapped.
        val capped = if (maxEntries < 0) sorted else sorted.take(maxEntries)
        val entries = capped.mapIndexed { index, (uuid, value) ->
            LeaderboardEntry(index + 1, uuid, value)
        }

        snapshot = LeaderboardSnapshot(entries, ranks, sorted.size, System.currentTimeMillis())
    }

    internal fun clear() {
        snapshot = LeaderboardSnapshot.EMPTY
        values?.clear()
    }
}
