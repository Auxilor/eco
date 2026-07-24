package com.willfp.eco.core.integrations.hologram;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for holograms.
 */
public interface Hologram {
    /**
     * Remove the hologram.
     */
    void remove();

    /**
     * Set the hologram contents.
     *
     * @param contents The contents.
     */
    void setContents(@NotNull List<String> contents);

    /**
     * Hide the hologram from a specific player, without affecting other viewers.
     *
     * @param player The player to hide the hologram from.
     */
    default void hide(@NotNull Player player) {
        // No-op by default.
    }

    /**
     * Show the hologram to a specific player after it was hidden with {@link #hide(Player)}.
     *
     * @param player The player to show the hologram to.
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
     * @return The location, or null if unsupported.
     */
    default Location getLocation() {
        return null;
    }
}
