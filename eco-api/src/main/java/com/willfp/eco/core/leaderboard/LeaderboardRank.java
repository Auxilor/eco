package com.willfp.eco.core.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A player's standing on a leaderboard: either an exact position, a percentile band, or
 * unranked.
 * <p>
 * Percentile bands exist because an exact position stops being meaningful on a large
 * playerbase: a player is better told they are in the top 5% than that they are #4,182. An
 * exact cutoff of zero means positions are always reported exactly, however large they get.
 */
public final class LeaderboardRank {
    /**
     * The shared unranked instance.
     */
    private static final LeaderboardRank UNRANKED = new LeaderboardRank(null, null);

    /**
     * The exact rank, one-indexed, or null if this is not an exact rank.
     */
    private final Integer rank;

    /**
     * The percentile, or null if this is not a percentile.
     */
    private final Double percent;

    /**
     * Create a new leaderboard rank.
     *
     * @param rank    The exact rank, or null.
     * @param percent The percentile, or null.
     */
    private LeaderboardRank(@Nullable final Integer rank,
                            @Nullable final Double percent) {
        this.rank = rank;
        this.percent = percent;
    }

    /**
     * Create an exact rank.
     *
     * @param rank The rank, one-indexed.
     * @return The leaderboard rank.
     */
    @NotNull
    public static LeaderboardRank exact(final int rank) {
        return new LeaderboardRank(rank, null);
    }

    /**
     * Create a percentile rank.
     *
     * @param percent The percentile, where a lower value is a better standing.
     * @return The leaderboard rank.
     */
    @NotNull
    public static LeaderboardRank percent(final double percent) {
        return new LeaderboardRank(null, percent);
    }

    /**
     * Get the unranked standing, for players with no leaderboard position.
     *
     * @return The leaderboard rank.
     */
    @NotNull
    public static LeaderboardRank unranked() {
        return UNRANKED;
    }

    /**
     * Create a standing from a raw rank.
     * <p>
     * Ranks at or inside the exact cutoff are reported exactly; ranks beyond it are converted to
     * a percentile, because a large playerbase is better told "top 5%" than "#4,182". An exact
     * cutoff of zero means the rank is always reported exactly.
     *
     * @param rank           The rank, one-indexed, or null if the player is unranked.
     * @param trackedPlayers The amount of players that were ranked.
     * @param exactCutoff    The highest rank to report exactly, or zero to always report exactly.
     * @param decimalPlaces  The amount of decimal places to round the percentile to.
     * @return The leaderboard rank.
     */
    @NotNull
    public static LeaderboardRank of(@Nullable final Integer rank,
                                     final int trackedPlayers,
                                     final int exactCutoff,
                                     final int decimalPlaces) {
        if (rank == null) {
            return unranked();
        }

        if (exactCutoff == 0 || rank <= exactCutoff) {
            return exact(rank);
        }

        if (trackedPlayers <= 0) {
            return unranked();
        }

        double factor = Math.pow(10, decimalPlaces);
        double raw = ((double) rank / (double) trackedPlayers) * 100.0;

        return percent(Math.round(raw * factor) / factor);
    }

    /**
     * Get if this is an exact rank.
     *
     * @return If exact.
     */
    public boolean isExact() {
        return this.rank != null;
    }

    /**
     * Get if this is a percentile.
     *
     * @return If a percentile.
     */
    public boolean isPercent() {
        return this.percent != null;
    }

    /**
     * Get if the player is unranked.
     *
     * @return If unranked.
     */
    public boolean isUnranked() {
        return this.rank == null && this.percent == null;
    }

    /**
     * Get the exact rank.
     *
     * @return The rank, or null if this is not an exact rank.
     */
    @Nullable
    public Integer getRank() {
        return this.rank;
    }

    /**
     * Get the percentile.
     *
     * @return The percentile, or null if this is not a percentile.
     */
    @Nullable
    public Double getPercent() {
        return this.percent;
    }
}
