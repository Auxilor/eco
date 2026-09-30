package com.willfp.eco.core.leaderboard;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Counts the players in each bucket of a {@link PlayerbaseTally}.
 * <p>
 * Called off the main thread on the leaderboard refresh schedule, with every uuid that has saved
 * profile data. Implementations must be thread-safe and must not call the Bukkit API.
 * <p>
 * <strong>Do not read through {@link com.willfp.eco.core.data.Profile}.</strong>
 * {@code PlayerProfile.load(uuid)} inserts into the profile handler's loaded-profile map, which
 * is only cleared when a player quits, so a playerbase-wide scan through {@code profile} retains
 * one profile object per uuid for the lifetime of the server. Use
 * {@link com.willfp.eco.core.Eco#readAllProfileValues(Set, com.willfp.eco.core.data.keys.PersistentDataKey)}
 * instead, which reads in bulk without loading or retaining anything.
 * <p>
 * <strong>Make sure the read you use has no side effects.</strong> A convenience getter that
 * migrates legacy data inline, or writes back a normalised value, is harmless when a player calls
 * it once on join and catastrophic here: this runs across the entire playerbase, on a schedule,
 * so one hidden write per player becomes a full-playerbase write every refresh interval. Read the
 * stored value directly rather than going through such a getter.
 */
@FunctionalInterface
public interface TallyProvider {
    /**
     * Count the players in each bucket.
     * <p>
     * Buckets with no players may be omitted or reported as zero; both read back as zero.
     *
     * @param uuids Every uuid with saved profile data.
     * @return The count of players in each bucket, keyed by bucket.
     */
    @NotNull
    Map<String, Integer> countBuckets(@NotNull Set<UUID> uuids);
}
