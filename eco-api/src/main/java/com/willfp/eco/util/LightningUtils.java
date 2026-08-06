package com.willfp.eco.util;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for lightning.
 */
public final class LightningUtils {
    /**
     * Strike lightning on a victim without fire.
     *
     * @param victim The entity to smite.
     * @param damage The damage to deal, in half-hearts.
     * @param silent Ignored; the lightning sound is now always played client-side.
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
     * <p>
     * Only the lightning effect is played, so no fire is started and no entities other than the
     * victim are damaged. The damage is applied to the victim directly.
     *
     * @param victim The entity to smite.
     * @param damage The damage to deal, in half-hearts.
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
