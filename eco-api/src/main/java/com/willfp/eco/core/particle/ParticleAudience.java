package com.willfp.eco.core.particle;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Determines which players see a particle.
 *
 * <p>Functional interface — implementations may be lambdas. Several built-in
 * audiences are exposed as constants and factory methods.</p>
 *
 * <p>Three identity-checked sentinels guide propagation:</p>
 * <ul>
 *   <li>{@link #DEFAULT} — "use the configured audience" (caller didn't pick one).</li>
 *   <li>{@link #WORLD} — broadcast to every player in the origin's world.</li>
 *   <li>{@link #CONTEXT_PLAYER} — only the player from the {@link PlaceholderContext}.</li>
 * </ul>
 */
@FunctionalInterface
public interface ParticleAudience {

    /**
     * Resolve the viewers for a single spawn.
     *
     * @param origin  The location the particle is being spawned at.
     * @param context The placeholder context (may carry a player).
     * @return The viewers; empty collection means "spawn nothing".
     */
    @NotNull
    Collection<Player> getViewers(@NotNull Location origin, @NotNull PlaceholderContext context);

    /** Sentinel: "use the configured audience". Identity-compared, never invoked. */
    @NotNull
    ParticleAudience DEFAULT = (origin, ctx) -> Collections.emptyList();

    /** Broadcast to every player currently in the origin's world. */
    @NotNull
    ParticleAudience WORLD = (origin, ctx) -> {
        if (origin.getWorld() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(origin.getWorld().getPlayers());
    };

    /** The player from the {@link PlaceholderContext}, or empty if there is none. */
    @NotNull
    ParticleAudience CONTEXT_PLAYER = (origin, ctx) -> {
        Player p = ctx.getPlayer();
        return p == null ? Collections.emptyList() : List.of(p);
    };

    /**
     * @param p The recipient.
     * @return Audience that sends to a single explicit player.
     */
    @NotNull
    static ParticleAudience player(@NotNull Player p) {
        return (origin, ctx) -> List.of(p);
    }

    /**
     * @param ps The recipients (snapshot taken on each invocation).
     * @return Audience that sends to a fixed collection of players.
     */
    @NotNull
    static ParticleAudience players(@NotNull Collection<Player> ps) {
        return (origin, ctx) -> new ArrayList<>(ps);
    }

    /**
     * @param p The excluded player.
     * @return Audience that broadcasts to the world EXCEPT the given player.
     */
    @NotNull
    static ParticleAudience except(@NotNull Player p) {
        return (origin, ctx) -> {
            if (origin.getWorld() == null) {
                return Collections.emptyList();
            }
            List<Player> result = new ArrayList<>(origin.getWorld().getPlayers());
            result.remove(p);
            return result;
        };
    }

    /**
     * @param radius The radius in blocks.
     * @return Audience that broadcasts only to players within {@code radius} of the origin.
     */
    @NotNull
    static ParticleAudience within(double radius) {
        double r2 = radius * radius;
        return (origin, ctx) -> {
            if (origin.getWorld() == null) {
                return Collections.emptyList();
            }
            return origin.getWorld().getPlayers().stream()
                    .filter(p -> p.getLocation().distanceSquared(origin) <= r2)
                    .toList();
        };
    }
}