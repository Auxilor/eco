package com.willfp.eco.core.particle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Create particles.
 */
public interface ParticleFactory {
    /**
     * Get the names (how the particle looks in lookup strings).
     * <p>
     * For example, for RGB particles this would be 'rgb', 'color', etc.
     *
     * @return The allowed names.
     */
    @NotNull List<String> getNames();

    /**
     * Create the particle
     *
     * @param key The key.
     * @return The particle.
     */
    @Nullable SpawnableParticle create(@NotNull String key);
}
