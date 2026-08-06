package com.willfp.eco.util;

import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for potions.
 *
 * @deprecated Legacy potion data is no longer supported. Read the duration from the
 * {@link org.bukkit.potion.PotionEffect} itself instead.
 */
@Deprecated(since = "6.77.0", forRemoval = true)
@SuppressWarnings("DeprecatedIsStillUsed")
public final class PotionUtils {
    /**
     * Get the duration (in ticks) for potion data.
     * <p>
     * This is a stub that ignores the data and always returns 1 tick. Legacy potion data is no
     * longer supported, so read the duration from the {@link org.bukkit.potion.PotionEffect}
     * itself instead.
     *
     * @param data The data.
     * @return Always 1.
     */
    @SuppressWarnings("removal")
    public static int getDuration(@NotNull final org.bukkit.potion.PotionData data) {
        return 1;
    }

    private PotionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
