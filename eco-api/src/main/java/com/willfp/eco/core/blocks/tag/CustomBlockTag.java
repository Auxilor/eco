package com.willfp.eco.core.blocks.tag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A custom block tag.
 */
public abstract class CustomBlockTag implements BlockTag {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * Create a new custom block tag.
     *
     * @param key The key.
     */
    public CustomBlockTag(@NotNull final NamespacedKey key) {
        this.key = key;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return key.toString();
    }
}
