package com.willfp.eco.core.leaderboard;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * A count of how many players fall into each of a set of buckets, refreshed on the leaderboard
 * schedule.
 * <p>
 * Use this for "how many players are X" questions that have no ordering: how many players have a
 * job active, how many are in each rank, how many have completed a quest. A {@link Leaderboard}
 * answers a different question -- who is top, and where does this player stand -- and modelling a
 * plain count as a leaderboard would invent an ordering that the underlying data does not have.
 * <p>
 * Like a leaderboard, every read here is a plain lookup against the last published
 * {@link TallyCounts}: reads never block, never compute, and never do I/O, so they are safe from
 * the main thread, from a placeholder, and from GUI lore. The cost is the same too -- reads are
 * as fresh as the last refresh, and no fresher.
 */
public interface PlayerbaseTally {
    /**
     * Get the ID of the tally.
     * <p>
     * Fully qualified as {@code plugin:id}, matching the key it is registered and looked up
     * under.
     *
     * @return The ID.
     */
    @NotNull
    String getId();

    /**
     * Get the plugin that owns the tally.
     *
     * @return The plugin.
     */
    @NotNull
    EcoPlugin getPlugin();

    /**
     * Get the last published counts.
     * <p>
     * Returns {@link TallyCounts#EMPTY} until the tally has been refreshed for the first time, so
     * this never returns null and never blocks.
     *
     * @return The counts.
     */
    @NotNull
    TallyCounts getCounts();

    /**
     * Get the count of players in a bucket.
     *
     * @param bucket The bucket.
     * @return The count, or zero if the bucket is unknown.
     */
    default int getCount(@NotNull final String bucket) {
        return this.getCounts().get(bucket);
    }

    /**
     * Refresh the tally now, rather than waiting for the next scheduled refresh.
     * <p>
     * The recount happens off the main thread. The new counts are published when the returned
     * future completes; until then, reads keep answering from the previous counts.
     *
     * @return A future completed once the new counts have been published.
     */
    @NotNull
    CompletableFuture<Void> refresh();
}
