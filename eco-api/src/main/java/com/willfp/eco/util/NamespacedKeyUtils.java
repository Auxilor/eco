package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utilities / API methods for {@link NamespacedKey}s.
 */
@UtilityClass
public class NamespacedKeyUtils {
    /**
     * Create a NamespacedKey for eco.
     *
     * @param string The string.
     * @return The key.
     */
    @NotNull
    public NamespacedKey createEcoKey(@NotNull final String string) {
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
    public NamespacedKey create(@NotNull final String namespace,
                                @NotNull final String key) {
        return Objects.requireNonNull(NamespacedKey.fromString(namespace + ":" + key));
    }
}
