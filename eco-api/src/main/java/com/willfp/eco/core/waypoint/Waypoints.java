package com.willfp.eco.core.waypoint;

import com.willfp.eco.core.Eco;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities to show waypoints on a player's locator bar.
 * <p>
 * Waypoints are per-viewer and are not persisted: if the player relogs, they
 * must be shown again.
 */
public final class Waypoints {
    /**
     * Show a waypoint to a player.
     *
     * @param viewer   The player to show it to.
     * @param id       The waypoint ID, used later to hide it.
     * @param location The waypoint location.
     * @param color    The colour of the marker, or null for the default.
     */
    public static void show(@NotNull final Player viewer,
                            @NotNull final UUID id,
                            @NotNull final Location location,
                            @Nullable final Color color) {
        Eco.get().showWaypoint(viewer, id, location, color == null ? null : color.asRGB());
    }

    /**
     * Hide a waypoint previously shown to a player.
     *
     * @param viewer The player.
     * @param id     The waypoint ID.
     */
    public static void hide(@NotNull final Player viewer,
                            @NotNull final UUID id) {
        Eco.get().hideWaypoint(viewer, id);
    }

    private Waypoints() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
