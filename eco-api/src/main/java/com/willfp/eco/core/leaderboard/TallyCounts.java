package com.willfp.eco.core.leaderboard;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An immutable point-in-time set of per-bucket player counts.
 * <p>
 * This is the tally equivalent of {@link LeaderboardSnapshot}: it is built off the main thread
 * and published by a single volatile reference swap, so it must be genuinely immutable rather
 * than a view over a map that the builder still holds.
 */
public final class TallyCounts {
    /**
     * Empty counts, with no buckets.
     * <p>
     * Answers every read without throwing, so it can be used as a placeholder before the first
     * refresh has happened.
     */
    public static final TallyCounts EMPTY = new TallyCounts(Map.of());

    /**
     * The count of players in each bucket, keyed by bucket.
     */
    private final Map<String, Integer> buckets;

    /**
     * The sum of every bucket count.
     */
    private final int total;

    /**
     * Create new counts.
     *
     * @param buckets The already-copied bucket counts.
     */
    private TallyCounts(@NotNull final Map<String, Integer> buckets) {
        this.buckets = buckets;

        int sum = 0;

        for (Integer count : buckets.values()) {
            sum += count;
        }

        this.total = sum;
    }

    /**
     * Create counts from a map of bucket to count.
     * <p>
     * The map is copied, not wrapped, so the caller may retain and mutate the original without
     * affecting the counts. This matters: counts are published to concurrent readers by a
     * volatile swap, and an unmodifiable <i>view</i> over a map the builder still mutates would
     * be neither immutable nor safely published.
     *
     * @param buckets The count of players in each bucket, keyed by bucket.
     * @return The counts.
     */
    @NotNull
    public static TallyCounts of(@NotNull final Map<String, Integer> buckets) {
        return new TallyCounts(Map.copyOf(buckets));
    }

    /**
     * Get the count of players in a bucket.
     *
     * @param bucket The bucket.
     * @return The count, or zero if the bucket is unknown.
     */
    public int get(@NotNull final String bucket) {
        return this.buckets.getOrDefault(bucket, 0);
    }

    /**
     * Get the sum of every bucket count.
     * <p>
     * This is a total of <strong>memberships, not of distinct players</strong>. A player who is
     * in two buckets is counted in both, and so contributes two to this total, because that is
     * what each of those per-bucket counts means. If your buckets are mutually exclusive then
     * this is also the amount of players; if they are not, it is not, and there is no way to
     * recover the distinct count from per-bucket totals.
     *
     * @return The total.
     */
    public int getTotal() {
        return this.total;
    }

    /**
     * Get the count of players in every bucket.
     *
     * @return The counts, keyed by bucket, unmodifiable.
     */
    @NotNull
    public Map<String, Integer> getBuckets() {
        return this.buckets;
    }
}
