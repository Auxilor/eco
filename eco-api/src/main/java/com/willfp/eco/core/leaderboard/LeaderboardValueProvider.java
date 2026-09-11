package com.willfp.eco.core.leaderboard;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Reads the values that a leaderboard ranks players by.
 * <p>
 * Called off the main thread on the refresh schedule, with every uuid that has saved profile
 * data. Implementations must be thread-safe and must not call the Bukkit API.
 * <p>
 * <strong>Do not read through {@link com.willfp.eco.core.data.Profile}.</strong>
 * {@code PlayerProfile.load(uuid)} inserts into the profile handler's loaded-profile map, which
 * is only cleared when a player quits, so a playerbase-wide scan through {@code profile} retains
 * one profile object per uuid for the lifetime of the server. Use
 * {@link com.willfp.eco.core.Eco#readAllProfileValues(Set, com.willfp.eco.core.data.keys.PersistentDataKey)}
 * instead, which reads in bulk without loading or retaining anything.
 */
@FunctionalInterface
public interface LeaderboardValueProvider {
    /**
     * Read the values to rank by.
     * <p>
     * Returning fewer uuids than were given is expected and supported: the uuids that are left
     * out are simply unranked.
     *
     * @param uuids Every uuid with saved profile data.
     * @return The values, keyed by uuid.
     */
    @NotNull
    Map<UUID, Double> readValues(@NotNull Set<UUID> uuids);
}
