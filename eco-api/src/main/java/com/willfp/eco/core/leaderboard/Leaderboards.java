package com.willfp.eco.core.leaderboard;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to manage leaderboards.
 */
public final class Leaderboards {
    /**
     * Register a leaderboard with a custom value provider.
     * <p>
     * The leaderboard is registered under {@code plugin:id}, so registering the same ID for the
     * same plugin again replaces the existing leaderboard rather than adding a second one.
     *
     * @param plugin   The plugin that owns the leaderboard.
     * @param id       The ID of the leaderboard, unique within the plugin.
     * @param provider The provider of the values to rank by.
     * @return The leaderboard.
     */
    @NotNull
    public static Leaderboard register(@NotNull final EcoPlugin plugin,
                                       @NotNull final String id,
                                       @NotNull final LeaderboardValueProvider provider) {
        return Eco.get().registerLeaderboard(plugin, id, provider);
    }

    /**
     * Register a leaderboard ranking players by a numeric {@link PersistentDataKey}.
     * <p>
     * This is the common case. Players with no stored value are left unranked.
     * <p>
     * The key must be of a numeric type ({@code INT}, {@code DOUBLE} or {@code BIG_DECIMAL}).
     * A key of any other type is rejected here rather than registering a leaderboard that would
     * query the database on every refresh and rank nobody; rank by something else with
     * {@link #register(EcoPlugin, String, LeaderboardValueProvider)} instead.
     *
     * @param plugin The plugin that owns the leaderboard.
     * @param id     The ID of the leaderboard, unique within the plugin.
     * @param key    The key to rank by.
     * @return The leaderboard.
     * @throws IllegalArgumentException If the key is of a type that can never yield a number.
     */
    @NotNull
    public static Leaderboard ofKey(@NotNull final EcoPlugin plugin,
                                    @NotNull final String id,
                                    @NotNull final PersistentDataKey<?> key) {
        return Eco.get().registerKeyLeaderboard(plugin, id, key);
    }

    /**
     * Get a leaderboard by its ID.
     *
     * @param id The fully qualified ID, as {@code plugin:id}.
     * @return The leaderboard, or null if none is registered under that ID.
     */
    @Nullable
    public static Leaderboard get(@NotNull final String id) {
        return Eco.get().getLeaderboard(id);
    }

    /**
     * Get every registered leaderboard.
     *
     * @return The leaderboards.
     */
    @NotNull
    public static Collection<Leaderboard> values() {
        return Eco.get().getLeaderboards();
    }

    /**
     * Register a playerbase tally with a custom provider.
     * <p>
     * The tally is registered under {@code plugin:id}, so registering the same ID for the same
     * plugin again replaces the existing tally rather than adding a second one.
     * <p>
     * Tallies are refreshed on the same cycle as leaderboards, sharing the same single
     * enumeration of the playerbase, so registering one costs nothing extra beyond the provider's
     * own work.
     *
     * @param plugin   The plugin that owns the tally.
     * @param id       The ID of the tally, unique within the plugin.
     * @param provider The provider of the bucket counts.
     * @return The tally.
     */
    @NotNull
    public static PlayerbaseTally registerTally(@NotNull final EcoPlugin plugin,
                                                @NotNull final String id,
                                                @NotNull final TallyProvider provider) {
        return Eco.get().registerTally(plugin, id, provider);
    }

    /**
     * Get a playerbase tally by its ID.
     *
     * @param id The fully qualified ID, as {@code plugin:id}.
     * @return The tally, or null if none is registered under that ID.
     */
    @Nullable
    public static PlayerbaseTally getTally(@NotNull final String id) {
        return Eco.get().getTally(id);
    }

    /**
     * Get every registered playerbase tally.
     *
     * @return The tallies.
     */
    @NotNull
    public static Collection<PlayerbaseTally> tallies() {
        return Eco.get().getTallies();
    }

    /**
     * Unregister every leaderboard owned by a plugin.
     * <p>
     * Call this at the top of your reload handler, before re-registering. Registration is keyed
     * {@code plugin:id}, so re-registering the same ID replaces rather than appends, but a
     * leaderboard whose config element was renamed or deleted has no replacement coming and
     * would otherwise be left behind, still holding its snapshot and still being refreshed, for
     * the rest of the server's uptime. Without this, every reload leaves one dead leaderboard
     * per removed config element.
     * <p>
     * This clears playerbase tallies owned by the plugin too, so it is the only call a reload
     * handler needs.
     *
     * @param plugin The plugin.
     */
    public static void unregisterAll(@NotNull final EcoPlugin plugin) {
        Eco.get().unregisterLeaderboards(plugin);
    }

    private Leaderboards() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
