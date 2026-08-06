package com.willfp.eco.core.integrations.hologram;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A floating text hologram.
 * <p>
 * Holograms are created through {@link HologramManager} and are backed by eco's own packet-based
 * text display implementation, so no third-party hologram plugin is required.
 *
 * @see HologramManager#createHologram(Location, HologramOptions)
 */
public interface Hologram {
    /**
     * Remove the hologram.
     * <p>
     * The hologram cannot be shown again after this; create a new one instead.
     */
    void remove();

    /**
     * Set the hologram contents.
     *
     * @param contents The contents, one entry per line, top to bottom.
     */
    void setContents(@NotNull List<String> contents);

    /**
     * Hide the hologram from a specific player, without affecting other viewers.
     *
     * @param player The player to hide the hologram from.
     * @see HologramOptions#isVisibleByDefault()
     */
    default void hide(@NotNull Player player) {
        // No-op by default.
    }

    /**
     * Show the hologram to a specific player after it was hidden with {@link #hide(Player)}.
     *
     * @param player The player to show the hologram to.
     * @see HologramOptions#isVisibleByDefault()
     */
    default void show(@NotNull Player player) {
        // No-op by default.
    }

    /**
     * Move the hologram to a new location.
     *
     * @param location The new location.
     */
    default void setLocation(@NotNull Location location) {
        // No-op by default.
    }

    /**
     * Get the hologram's current location.
     *
     * @return The location, or null if the implementation does not track it.
     */
    default Location getLocation() {
        return null;
    }
}
