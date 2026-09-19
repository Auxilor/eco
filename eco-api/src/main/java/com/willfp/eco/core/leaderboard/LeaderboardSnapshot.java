package com.willfp.eco.core.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An immutable point-in-time view of a leaderboard.
 * <p>
 * The retained entries may be capped (by a max-entries setting) while the rank lookup is
 * uncapped, so a player can always find their own rank even when they fall outside the retained
 * top-N. In other words, {@link #getRank(UUID)} can return a rank for which
 * {@link #getEntry(int)} has no entry.
 */
public final class LeaderboardSnapshot {
    /**
     * An empty snapshot, tracking no players.
     * <p>
     * Answers every read without throwing, so it can be used as a placeholder before the first
     * leaderboard has been built.
     */
    public static final LeaderboardSnapshot EMPTY = new LeaderboardSnapshot(List.of(), Map.of(), 0, 0L);

    /**
     * The retained entries, in leaderboard order.
     */
    private final List<LeaderboardEntry> entries;

    /**
     * The rank of every tracked player, one-indexed.
     */
    private final Map<UUID, Integer> ranks;

    /**
     * The amount of players that were ranked.
     */
    private final int trackedPlayers;

    /**
     * The time at which the snapshot was built, in milliseconds.
     */
    private final long builtAt;

    /**
     * Create a new leaderboard snapshot.
     * <p>
     * Both collections are copied, so the caller may retain and mutate the originals without
     * affecting the snapshot.
     *
     * @param entries        The retained entries, in leaderboard order. May be capped.
     * @param ranks          The rank of every tracked player, one-indexed. Never capped.
     * @param trackedPlayers The amount of players that were ranked.
     * @param builtAt        The time at which the snapshot was built, in milliseconds.
     */
    public LeaderboardSnapshot(@NotNull final List<LeaderboardEntry> entries,
                               @NotNull final Map<UUID, Integer> ranks,
                               final int trackedPlayers,
                               final long builtAt) {
        this.entries = List.copyOf(entries);
        this.ranks = Map.copyOf(ranks);
        this.trackedPlayers = trackedPlayers;
        this.builtAt = builtAt;
    }

    /**
     * Get the entry at a position.
     * <p>
     * Positions are one-indexed, so the top entry is at position 1. Anything outside the retained
     * entries, including zero and negative positions, returns null.
     *
     * @param position The position, one-indexed.
     * @return The entry, or null if there is no retained entry at that position.
     */
    @Nullable
    public LeaderboardEntry getEntry(final int position) {
        if (position < 1 || position > this.entries.size()) {
            return null;
        }

        return this.entries.get(position - 1);
    }

    /**
     * Get the rank of a player.
     * <p>
     * Ranks are one-indexed and uncapped, so a player outside the retained entries still has a
     * rank.
     *
     * @param uuid The UUID of the player.
     * @return The rank, or null if the player was not ranked.
     */
    @Nullable
    public Integer getRank(@NotNull final UUID uuid) {
        return this.ranks.get(uuid);
    }

    /**
     * Get the retained entries, in leaderboard order.
     *
     * @return The entries, unmodifiable.
     */
    @NotNull
    public List<LeaderboardEntry> getEntries() {
        return this.entries;
    }

    /**
     * Get the amount of players that were ranked.
     * <p>
     * This is not capped by the amount of retained entries.
     *
     * @return The amount of tracked players.
     */
    public int getTrackedPlayers() {
        return this.trackedPlayers;
    }

    /**
     * Get the time at which the snapshot was built, in milliseconds.
     *
     * @return The build time.
     */
    public long getBuiltAt() {
        return this.builtAt;
    }

    /**
     * Get if the snapshot tracks no players.
     *
     * @return If the snapshot is empty.
     */
    public boolean isEmpty() {
        return this.trackedPlayers == 0;
    }
}
