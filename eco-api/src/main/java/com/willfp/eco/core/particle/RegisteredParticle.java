package com.willfp.eco.core.particle;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Snapshot of a registered particle. Returned from {@link Particles#dump()}
 * for diagnostics. {@code particle} is the same instance held in the registry.
 *
 * @param key      The namespaced key.
 * @param particle The spawnable particle.
 * @param origin   Where it came from.
 * @param owner    The plugin that registered it (may be null for legacy entries).
 */
public record RegisteredParticle(
        @NotNull NamespacedKey key,
        @NotNull SpawnableParticle particle,
        @NotNull ParticleOrigin origin,
        @Nullable Plugin owner
) {}