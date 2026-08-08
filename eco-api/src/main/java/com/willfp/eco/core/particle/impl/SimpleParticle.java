package com.willfp.eco.core.particle.impl;

import com.willfp.eco.core.particle.SpawnableParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A particle backed directly by a bukkit {@link Particle}, with no extra data.
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

    /**
     * Spawn the particle at a location.
     * <p>
     * Particles that require data which a bare particle name cannot carry are not spawned,
     * as spawning them without that data would throw.
     *
     * @param location The location.
     * @param amount   The amount to spawn.
     */
    @Override
    public void spawn(@NotNull final Location location,
                      final int amount) {
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        Class<?> dataType = particle.getDataType();

        if (dataType == Void.class) {
            world.spawnParticle(particle, location, amount, 0, 0, 0, 0);
        } else if (dataType == Float.class) {
            world.spawnParticle(particle, location, amount, 0, 0, 0, 0, 0f);
        } else if (dataType == Integer.class) {
            world.spawnParticle(particle, location, amount, 0, 0, 0, 0, 0);
        }

        /*
        Any other data type requires data that a bare particle name cannot carry, e.g. DUST
        needs a colour. Spawning it without data throws, so it is skipped here - use a
        particle factory instead, e.g. dust:00ff00 or dust_transition:ff0000:00ff00.
         */
    }
}
