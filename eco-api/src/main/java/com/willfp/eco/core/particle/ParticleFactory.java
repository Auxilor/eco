package com.willfp.eco.core.particle;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Create the particle.
     * <p>
     * The key is the part of a lookup string after the colon, e.g. for {@code rgb:00ff00}
     * the key would be {@code 00ff00}.
     *
     * @param key The key.
     * @return The particle, or null if the key is invalid.
     */
    @Nullable SpawnableParticle create(@NotNull String key);
}
