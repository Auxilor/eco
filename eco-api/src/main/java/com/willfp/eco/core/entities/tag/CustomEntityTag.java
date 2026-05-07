package com.willfp.eco.core.entities.tag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A custom entity tag.
 */
public abstract class CustomEntityTag implements EntityTag {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * Create a new custom entity tag.
     *
     * @param key The key.
     */
    public CustomEntityTag(@NotNull final NamespacedKey key) {
        this.key = key;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return key.toString();
    }
}