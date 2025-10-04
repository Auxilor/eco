package com.willfp.eco.util;

import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for potions.
 */
@Deprecated(since = "6.77.0", forRemoval = true)
@SuppressWarnings("DeprecatedIsStillUsed")
public final class PotionUtils {
    /**
     * Get the duration (in ticks) for potion data.
     *
     * @param data The data.
     * @return The duration.
     */
    @SuppressWarnings("removal")
    public static int getDuration(@NotNull final org.bukkit.potion.PotionData data) {
        return 1;
    }

    private PotionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
