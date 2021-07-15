package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class NamespacedKeyUtils {
    /**
     * Create a NamespacedKey for eco.
     *
     * @param string The string.
     * @return The key.
     */
    public NamespacedKey createEcoKey(@NotNull final String string) {
        return Objects.requireNonNull(NamespacedKey.fromString("eco:" + string));
    }
}
