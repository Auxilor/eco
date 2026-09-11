package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.leaderboard.PlayerbaseTally
import com.willfp.eco.core.leaderboard.TallyCounts
import com.willfp.eco.core.leaderboard.TallyProvider
import java.util.UUID
import java.util.concurrent.CompletableFuture

class EcoPlayerbaseTally(
    private val id: String,
    private val owner: EcoPlugin,
    private val provider: TallyProvider,
    private val service: LeaderboardService
) : PlayerbaseTally {
    // Written on the refresh executor, read from the main thread (placeholders, GUI lore), so
    // publication of the new counts has to be guaranteed. The counts themselves are immutable,
    // so a volatile reference swap is all the synchronisation a reader needs.
    @Volatile
    private var counts: TallyCounts = TallyCounts.EMPTY

    override fun getId() = id

    override fun getPlugin() = owner

    override fun getCounts() = counts

    override fun refresh(): CompletableFuture<Void> = service.refresh(this)

    /** Recount the playerbase. Called only from the refresh executor. */
    internal fun rebuild(uuids: Set<UUID>) {
        counts = TallyCounts.of(provider.countBuckets(uuids))
    }

    internal fun clear() {
        counts = TallyCounts.EMPTY
    }
}
