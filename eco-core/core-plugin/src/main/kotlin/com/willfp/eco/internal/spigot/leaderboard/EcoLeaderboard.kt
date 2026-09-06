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

    override fun getRank(uuid: UUID): LeaderboardRank = LeaderboardRank.of(
        snapshot.getRank(uuid),
        snapshot.trackedPlayers,
        service.exactRankCutoff,
        service.percentDecimalPlaces
    )

    override fun refresh(): CompletableFuture<Void> = service.refresh(this)

    /** Build a new snapshot. Called only from the refresh executor. */
    internal fun rebuild(uuids: Set<UUID>, maxEntries: Int) {
        val values = provider.readValues(uuids)

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
    }
}
