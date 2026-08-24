package com.willfp.eco.core.particle;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A particle that can be spawned.
 */
public interface SpawnableParticle {
    /**
     * Spawn the particle at a location.
     *
     * @param location The location.
     * @param amount   The amount to spawn.
     */
    void spawn(@NotNull Location location,
               int amount);

    /**
     * Spawn the particle at a location.
     *
     * @param location The location.
     */
    default void spawn(@NotNull Location location) {
        spawn(location, 1);
    }

    /**
     * Spawn the particle at a location, visible only to a single player.
     * <p>
     * Implementations that do not override this spawn the particle for everyone, so
     * a particle written against an older version of eco still spawns rather than
     * silently doing nothing.
     *
     * @param player   The player to spawn the particle for.
     * @param location The location.
     * @param amount   The amount to spawn.
     */
    default void spawnTo(@NotNull Player player,
                         @NotNull Location location,
                         final int amount) {
        spawn(location, amount);
    }

    /**
     * Spawn the particle at a location, visible only to a single player.
     *
     * @param player   The player to spawn the particle for.
     * @param location The location.
     */
    default void spawnTo(@NotNull Player player,
                         @NotNull Location location) {
        spawnTo(player, location, 1);
    }

    /**
     * Spawn the particle at a location, visible only to the given players.
     *
     * @param players  The players to spawn the particle for.
     * @param location The location.
     * @param amount   The amount to spawn.
     */
    default void spawnTo(@NotNull Collection<? extends Player> players,
                         @NotNull Location location,
                         final int amount) {
        for (Player player : players) {
            spawnTo(player, location, amount);
        }
    }

    /**
     * Spawn the particle at a location, visible only to the given players.
     *
     * @param players  The players to spawn the particle for.
     * @param location The location.
     */
    default void spawnTo(@NotNull Collection<? extends Player> players,
                         @NotNull Location location) {
        spawnTo(players, location, 1);
    }
}
