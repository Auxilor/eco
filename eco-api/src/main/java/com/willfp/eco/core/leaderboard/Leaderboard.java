package com.willfp.eco.core.leaderboard;

import com.willfp.eco.core.EcoPlugin;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A leaderboard, ranking players by a value read from their profile data.
 * <p>
 * Every read on a leaderboard is a plain lookup against the last published
 * {@link LeaderboardSnapshot}: no I/O, no locking, and no loader. That is the central guarantee
 * of this API, and it is what makes leaderboard reads safe to do from the main thread, from
 * inside a placeholder, and from GUI lore rendering, all of which happen often enough that any
 * per-read work would be felt.
 * <p>
 * The cost is that reads are as fresh as the last refresh, and no fresher.
 */
public interface Leaderboard {
    /**
     * Get the ID of the leaderboard.
     * <p>
     * Fully qualified as {@code plugin:id}, matching the key it is registered and looked up
     * under.
     *
     * @return The ID.
     */
    @NotNull
    String getId();

    /**
     * Get the plugin that owns the leaderboard.
     *
     * @return The plugin.
     */
    @NotNull
    EcoPlugin getPlugin();

    /**
     * Get the last published snapshot.
     * <p>
     * Returns {@link LeaderboardSnapshot#EMPTY} until the leaderboard has been refreshed for the
     * first time, so this never returns null and never blocks.
     *
     * @return The snapshot.
     */
    @NotNull
    LeaderboardSnapshot getSnapshot();

    /**
     * Get the entry at a position.
     * <p>
     * Positions are one-indexed, so the top entry is at position 1.
     *
     * @param position The position, one-indexed.
     * @return The entry, or null if there is no retained entry at that position.
     */
    @Nullable
    default LeaderboardEntry getTop(final int position) {
        return this.getSnapshot().getEntry(position);
    }

    /**
     * Get the raw position of a player.
     * <p>
     * Uncapped, so a player outside the retained entries still has a position.
     *
     * @param uuid The UUID of the player.
     * @return The position, one-indexed, or null if the player is unranked.
     */
    @Nullable
    default Integer getPosition(@NotNull final UUID uuid) {
        return this.getSnapshot().getRank(uuid);
    }

    /**
     * Get the standing of a player, as an exact position, a percentile band, or unranked.
     *
     * @param uuid The UUID of the player.
     * @return The rank.
     */
    @NotNull
    LeaderboardRank getRank(@NotNull UUID uuid);

    /**
     * Refresh the leaderboard now, rather than waiting for the next scheduled refresh.
     * <p>
     * The rebuild happens off the main thread. The new snapshot is published when the returned
     * future completes; until then, reads keep answering from the previous snapshot.
     *
     * @return A future completed once the new snapshot has been published.
     */
    @NotNull
    CompletableFuture<Void> refresh();
}
