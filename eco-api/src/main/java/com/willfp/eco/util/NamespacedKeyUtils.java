package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return NamespacedKeyUtils.create("eco", string);
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
        return Eco.get().createNamespacedKey(
                namespace,
                key
        );
    }

    /**
     * Create a NamespacedKey from a string.
     * <p>
     * Preferred over {@link NamespacedKey#fromString(String)} for performance reasons.
     *
     * @param string The string.
     * @return The key.
     */
    @NotNull
    public static NamespacedKey fromString(@NotNull final String string) {
        return Objects.requireNonNull(NamespacedKeyUtils.fromStringOrNull(string));
    }

    /**
     * Create a NamespacedKey from a string.
     * <p>
     * Preferred over {@link NamespacedKey#fromString(String)} for performance reasons.
     *
     * @param string The string.
     * @return The key, or null if not a key.
     */
    @Nullable
    public static NamespacedKey fromStringOrNull(@NotNull final String string) {
        int index = string.indexOf(":");

        if (index < 0) {
            return null;
        }

        return NamespacedKeyUtils.create(
                string.substring(0, index),
                string.substring(index + 1)
        );
    }

    private NamespacedKeyUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
