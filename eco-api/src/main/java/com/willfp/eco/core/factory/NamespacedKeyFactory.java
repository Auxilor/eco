package com.willfp.eco.core.factory;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to create {@link NamespacedKey}s for a plugin.
 */
public interface NamespacedKeyFactory {
    /**
     * Create a {@link NamespacedKey} in the namespace of this factory's
     * {@link com.willfp.eco.core.EcoPlugin}.
     *
     * @param key The key in the {@link NamespacedKey}.
     * @return The created {@link NamespacedKey}.
     */
    @NotNull
    NamespacedKey create(@NotNull String key);
}
