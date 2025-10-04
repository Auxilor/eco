package com.willfp.eco.util;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for lightning.
 */
public final class LightningUtils {
    /**
     * Strike lightning on player without fire.
     *
     * @param victim The entity to smite.
     * @param damage The damage to deal.
     * @param silent If the lightning sound should be played locally
     * @deprecated Use {@link #strike(LivingEntity, double)} instead, sound is now client-side.
     */
    @Deprecated(since = "6.77.0", forRemoval = true)
    public static void strike(@NotNull final LivingEntity victim,
                              final double damage,
                              final boolean silent) {
        strike(victim, damage);
    }

    /**
     * Strike lightning on a victim without fire.
     *
     * @param victim The entity to smite.
     * @param damage The damage to deal.
     */
    public static void strike(@NotNull final LivingEntity victim,
                              final double damage) {
        Location loc = victim.getLocation();

        victim.getWorld().strikeLightningEffect(loc);

        victim.damage(damage);
    }

    private LightningUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
