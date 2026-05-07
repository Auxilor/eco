package com.willfp.eco.core.particle;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * A particle effect that can be spawned at a location.
 *
 * <p>Every effect — single bursts, shapes, animations — implements this
 * one interface. The returned {@link Cancellable} is the only handle to
 * stop an open-ended animation; for static spawns it is a no-op.</p>
 */
public interface SpawnableParticle {

    /**
     * Spawn the particle at a location.
     *
     * @param location The origin.
     * @param context  Variables and placeholders for math evaluation.
     * @param audience Who sees the particle. Pass {@link ParticleAudience#DEFAULT}
     *                 to use the configured audience (the most common choice).
     * @return A handle to cancel the effect. For static spawns the handle is
     *         already cancelled and {@code setCancelled(true)} is a no-op.
     */
    @NotNull
    Cancellable spawn(@NotNull Location location,
                      @NotNull PlaceholderContext context,
                      @NotNull ParticleAudience audience);

    /** Convenience overload: empty context, default audience. */
    @NotNull
    default Cancellable spawn(@NotNull Location location) {
        return spawn(location, PlaceholderContext.EMPTY, ParticleAudience.DEFAULT);
    }

    /** Convenience overload: custom context, default audience. */
    @NotNull
    default Cancellable spawn(@NotNull Location location,
                              @NotNull PlaceholderContext context) {
        return spawn(location, context, ParticleAudience.DEFAULT);
    }

    /** Convenience overload: empty context, custom audience. */
    @NotNull
    default Cancellable spawn(@NotNull Location location,
                              @NotNull ParticleAudience audience) {
        return spawn(location, PlaceholderContext.EMPTY, audience);
    }
}