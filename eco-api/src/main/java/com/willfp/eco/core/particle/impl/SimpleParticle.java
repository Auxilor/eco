package com.willfp.eco.core.particle.impl;

import com.willfp.eco.core.particle.SpawnableParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Empty (invalid) particle that is spawned when an invalid key is provided.
 */
public final class SimpleParticle implements SpawnableParticle {
    /**
     * The particle to be spawned.
     */
    private final Particle particle;

    /**
     * Create a new spawnable particle.
     *
     * @param particle The particle.
     */
    public SimpleParticle(@NotNull final Particle particle) {
        this.particle = particle;
    }

    @Override
    public void spawn(@NotNull final Location location,
                      final int amount) {
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(particle, location, amount, 0, 0, 0, 0, null);
    }
}
