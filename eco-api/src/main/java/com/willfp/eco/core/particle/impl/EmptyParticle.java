package com.willfp.eco.core.particle.impl;

import com.willfp.eco.core.particle.ParticleAudience;
import com.willfp.eco.core.particle.SpawnableParticle;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Failsafe particle returned when a lookup misses. Spawning it is a no-op.
 *
 * <p>Singleton — use {@link #INSTANCE}. Constructor retained as
 * deprecated for binary compatibility with prior callers.</p>
 */
public final class EmptyParticle implements SpawnableParticle {

    /** The singleton instance. */
    public static final EmptyParticle INSTANCE = new EmptyParticle();

    /**
     * @deprecated Use {@link #INSTANCE}. Kept for binary compatibility.
     */
    @Deprecated
    public EmptyParticle() {
        // no-op
    }

    private static final Cancellable NOOP = new Cancellable() {
        @Override public boolean isCancelled() { return true; }
        @Override public void setCancelled(boolean cancel) { /* no-op */ }
    };

    @NotNull
    @Override
    public Cancellable spawn(@NotNull final Location location,
                             @NotNull final PlaceholderContext context,
                             @NotNull final ParticleAudience audience) {
        return NOOP;
    }
}