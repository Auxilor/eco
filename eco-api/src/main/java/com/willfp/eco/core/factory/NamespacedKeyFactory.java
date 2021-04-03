package com.willfp.eco.core.factory;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface NamespacedKeyFactory {
    /**
     * Create an {@link NamespacedKey} associated with an {@link com.willfp.eco.core.EcoPlugin}.
     *
     * @param key The key in the {@link NamespacedKey}.
     * @return The created {@link NamespacedKey}.
     */
    NamespacedKey create(@NotNull String key);
}
