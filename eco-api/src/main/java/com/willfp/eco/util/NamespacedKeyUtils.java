package com.willfp.eco.util;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utilities / API methods for {@link NamespacedKey}s.
 */
public final class NamespacedKeyUtils {
    /**
     * Create a NamespacedKey for eco.
     *
     * @param string The string.
     * @return The key.
     */
    @NotNull
    public static NamespacedKey createEcoKey(@NotNull final String string) {
        return Objects.requireNonNull(NamespacedKey.fromString("eco:" + string));
    }

    /**
     * Create a NamespacedKey with any namespace and key.
     *
     * @param namespace The namespace.
     * @param key       The key.
     * @return The key.
     */
    @NotNull
    public static NamespacedKey create(@NotNull final String namespace,
                                       @NotNull final String key) {
        return Objects.requireNonNull(NamespacedKey.fromString(namespace + ":" + key));
    }

    private NamespacedKeyUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
