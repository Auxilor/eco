package com.willfp.eco.core.items.tag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A custom item tag.
 */
public abstract class CustomItemTag implements ItemTag {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * Create a new custom item tag.
     *
     * @param key         The key.
     */
    public CustomItemTag(@NotNull final NamespacedKey key) {
        this.key = key;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return key.toString();
    }
}
