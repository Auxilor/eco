package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;

/**
 * A placeholder represents a string that can hold a value.
 */
public sealed interface Placeholder permits PlayerPlaceholder, PlayerlessPlaceholder, InjectablePlaceholder {
    /**
     * Get the plugin that holds the placeholder.
     *
     * @return The plugin.
     */
    EcoPlugin getPlugin();

    /**
     * Get the identifier for the placeholder.
     *
     * @return The identifier.
     */
    String getIdentifier();
}
