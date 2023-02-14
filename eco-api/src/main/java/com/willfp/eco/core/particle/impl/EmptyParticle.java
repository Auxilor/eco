package com.willfp.eco.core.particle.impl;

import com.willfp.eco.core.particle.SpawnableParticle;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Empty (invalid) particle that is spawned when an invalid key is provided.
 */
public final class EmptyParticle implements SpawnableParticle {
    @Override
    public void spawn(@NotNull final Location location,
                      final int amount) {
        // Do nothing.
    }

    /**
     * Spawn the particle for a player at a location.
     *
     * @param location The location.
     * @param amount   The amount to spawn.
     * @param player   The player.
     */
    @Override
    public void spawn(@NotNull Location location, int amount, @NotNull Player player) {
        // Do nothing.
    }
}
