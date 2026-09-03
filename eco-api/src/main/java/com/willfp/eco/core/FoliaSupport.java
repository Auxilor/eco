package com.willfp.eco.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Guards features that Folia does not support.
 * <p>
 * Each feature is logged the first time it is reached on Folia, and only the first time:
 * a warning repeated once per call is a warning nobody reads.
 */
public final class FoliaSupport {
    /**
     * Features already logged, so that each is reported once per server.
     */
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    /**
     * Get if a feature is unavailable on this server, logging it the first time.
     * <p>
     * For features that can be skipped. Use {@link #requireSupported(String)} where
     * skipping would silently return a wrong answer.
     *
     * @param feature The feature, named as a user would recognise it.
     * @return If the feature is unavailable.
     */
    public static boolean isUnsupported(@NotNull final String feature) {
        if (!Prerequisite.HAS_FOLIA.isMet()) {
            return false;
        }

        warnOnce(feature);

        return true;
    }

    /**
     * Throw if a feature is unavailable on this server, logging it the first time.
     * <p>
     * For features with no meaningful degraded behaviour, where continuing would return
     * something wrong rather than something reduced.
     *
     * @param feature The feature, named as a user would recognise it.
     * @throws UnsupportedOperationException If running on Folia.
     */
    public static void requireSupported(@NotNull final String feature) {
        if (!Prerequisite.HAS_FOLIA.isMet()) {
            return;
        }

        warnOnce(feature);

        throw new UnsupportedOperationException(
                String.format("%s is not supported on Folia.", feature)
        );
    }

    private static void warnOnce(@NotNull final String feature) {
        if (!WARNED.add(feature)) {
            return;
        }

        Eco.get().getEcoPlugin().getLogger().warning(
                String.format("%s is not supported on Folia, so it cannot be used.", feature)
        );
    }

    private FoliaSupport() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
