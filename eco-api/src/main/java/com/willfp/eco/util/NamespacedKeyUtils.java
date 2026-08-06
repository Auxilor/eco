package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for {@link NamespacedKey}s.
 */
public final class NamespacedKeyUtils {
    /**
     * Create a {@link NamespacedKey} in the {@code eco} namespace.
     *
     * @param string The key, without a namespace.
     * @return The key.
     */
    @NotNull
    public static NamespacedKey createEcoKey(@NotNull final String string) {
        return NamespacedKeyUtils.create("eco", string);
    }

    /**
     * Create a {@link NamespacedKey} with any namespace and key.
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
     * Create a {@link NamespacedKey} from a string.
     * <p>
     * The string is split around the first colon into a namespace and a key.
     * <p>
     * Preferred over {@link NamespacedKey#fromString(String)} for performance reasons.
     *
     * @param string The string, in {@code namespace:key} form.
     * @return The key.
     * @throws NullPointerException If the string does not contain a colon.
     */
    @NotNull
    public static NamespacedKey fromString(@NotNull final String string) {
        return Objects.requireNonNull(NamespacedKeyUtils.fromStringOrNull(string));
    }

    /**
     * Create a {@link NamespacedKey} from a string.
     * <p>
     * The string is split around the first colon into a namespace and a key.
     * <p>
     * Preferred over {@link NamespacedKey#fromString(String)} for performance reasons.
     *
     * @param string The string, in {@code namespace:key} form.
     * @return The key, or null if the string does not contain a colon.
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
