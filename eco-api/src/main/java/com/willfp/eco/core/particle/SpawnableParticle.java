package com.willfp.eco.core.particle;

import net.kyori.adventure.audience.Audience;
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
     * Spawn the particle for a player at a location.
     *
     * @param location The location.
     * @param amount   The amount to spawn.
     * @param player The player.
     */
    void spawn(@NotNull Location location,
               int amount,
               @NotNull Player player);

    /**
     * Spawn the particle at a location.
     *
     * @param location The location.
     */
    default void spawn(@NotNull Location location) {
        spawn(location, 1);
    }
}
