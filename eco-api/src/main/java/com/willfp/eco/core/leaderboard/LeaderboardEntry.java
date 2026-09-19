package com.willfp.eco.core.leaderboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A single position on a leaderboard.
 * <p>
 * Entries hold no Bukkit state beyond a UUID, so they can be built and inspected off the main
 * thread and without a running server.
 */
public final class LeaderboardEntry {
    /**
     * The position, one-indexed.
     */
    private final int position;

    /**
     * The UUID of the player.
     */
    private final UUID uuid;

    /**
     * The value that the player was ranked by.
     */
    private final double value;

    /**
     * Create a new leaderboard entry.
     *
     * @param position The position, one-indexed.
     * @param uuid     The UUID of the player.
     * @param value    The value that the player was ranked by.
     */
    public LeaderboardEntry(final int position,
                            @NotNull final UUID uuid,
                            final double value) {
        this.position = position;
        this.uuid = uuid;
        this.value = value;
    }

    /**
     * Get the position on the leaderboard.
     * <p>
     * Positions are one-indexed, so the top entry is at position 1.
     *
     * @return The position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Get the UUID of the player.
     *
     * @return The UUID.
     */
    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Get the value that the player was ranked by.
     * <p>
     * Stored as a double for uniformity across plugins: a currency balance, a skill level and a
     * collection count are all ranked the same way. Format it for display with the plugin's own
     * formatter.
     *
     * @return The value.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Get the player.
     * <p>
     * Resolved on call rather than stored, so building a snapshot never touches the
     * OfflinePlayer cache.
     *
     * @return The player.
     */
    @NotNull
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }
}
