package com.willfp.eco.core.particle;

import com.willfp.eco.core.particle.impl.EmptyParticle;
import com.willfp.eco.core.particle.impl.SimpleParticle;
import com.willfp.eco.util.StringUtils;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to manage particles.
 */
public final class Particles {
    /**
     * All factories.
     */
    private static final Map<String, ParticleFactory> FACTORIES = new ConcurrentHashMap<>();

    /**
     * Register a new particle factory.
     *
     * @param factory The factory.
     */
    public static void registerParticleFactory(@NotNull final ParticleFactory factory) {
        for (String name : factory.getNames()) {
            FACTORIES.put(name.toLowerCase(), factory);
        }
    }

    /**
     * Lookup a particle from a string.
     * <p>
     * A particle string should look like {@code magic}, {@code rgb:00ff00}
     *
     * @param key The key.
     * @return The particle, or an {@link EmptyParticle} if invalid.
     */
    @NotNull
    public static SpawnableParticle lookup(@NotNull final String key) {
        String[] args = StringUtils.parseTokens(key.toLowerCase());

        if (args.length == 0) {
            return new EmptyParticle();
        }

        SpawnableParticle spawnableParticle;

        String[] split = args[0].split(":");

        if (split.length == 1) {
            try {
                Particle particle = Particle.valueOf(args[0].toUpperCase());
                spawnableParticle = new SimpleParticle(particle);
            } catch (IllegalArgumentException e) {
                spawnableParticle = new EmptyParticle();
            }
        } else if (split.length == 2) {
            String name = split[0];
            String factoryKey = split[1];

            ParticleFactory factory = FACTORIES.get(name);
            if (factory == null) {
                spawnableParticle = new EmptyParticle();
            } else {
                spawnableParticle = factory.create(factoryKey);
            }
        } else {
            return new EmptyParticle();
        }

        if (spawnableParticle == null || spawnableParticle instanceof EmptyParticle) {
            return new EmptyParticle();
        }

        return spawnableParticle;
    }

    private Particles() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
